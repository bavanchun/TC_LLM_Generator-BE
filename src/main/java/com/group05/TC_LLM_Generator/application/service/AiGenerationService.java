package com.group05.TC_LLM_Generator.application.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group05.TC_LLM_Generator.application.exception.LlmServiceException;
import com.group05.TC_LLM_Generator.application.port.out.LLMProviderPort;
import com.group05.TC_LLM_Generator.application.port.out.TestCaseRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.UserStoryRepositoryPort;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent;
import com.group05.TC_LLM_Generator.infrastructure.ai.dto.ChatMessage;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.dto.response.GenerateTestCasesResponse;
import com.group05.TC_LLM_Generator.presentation.dto.response.RefinedUserStoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for AI-powered features:
 * - Refine user stories (rewrite for professionalism)
 * - Generate test cases from user stories + acceptance criteria
 */
@Slf4j
@Service
public class AiGenerationService {

    private final LLMProviderPort llmProvider;
    private final UserStoryRepositoryPort userStoryRepository;
    private final TestCaseRepositoryPort testCaseRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public AiGenerationService(
            LLMProviderPort llmProvider,
            UserStoryRepositoryPort userStoryRepository,
            TestCaseRepositoryPort testCaseRepository,
            ApplicationEventPublisher eventPublisher) {
        this.llmProvider = llmProvider;
        this.userStoryRepository = userStoryRepository;
        this.testCaseRepository = testCaseRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // ─── Generate Acceptance Criteria (pre-save) ────────────────────

    /**
     * Generate acceptance criteria suggestions from story description fields.
     * Does NOT require an existing story in DB — used during story creation.
     */
    public List<String> generateAcceptanceCriteria(String title, String asA, String iWantTo, String soThat, String description) {
        String systemPrompt = """
                You are a senior QA engineer. Generate 3 to 5 clear, testable acceptance criteria \
                for the given user story. Each criterion should be specific and measurable. \
                Return ONLY a valid JSON object with key "criteria" containing an array of strings. \
                Do not add any extra text or explanation outside the JSON.""";

        String userPrompt = String.format("""
                Generate acceptance criteria for this user story:
                
                Title: %s
                As a: %s
                I want to: %s
                So that: %s
                Description: %s
                
                Return: {"criteria":["Given... When... Then...","..."]}""",
                nullSafe(title), nullSafe(asA), nullSafe(iWantTo), nullSafe(soThat), nullSafe(description));

        List<ChatMessage> messages = List.of(
                ChatMessage.builder().role("system").content(systemPrompt).build(),
                ChatMessage.builder().role("user").content(userPrompt).build()
        );

        try {
            String response = llmProvider.chatCompletion(messages, 0.3, 1024, true);
            String json = extractJson(response);
            JsonNode node = objectMapper.readTree(json);

            JsonNode criteriaNode = node.has("criteria") ? node.get("criteria") : node;
            if (!criteriaNode.isArray()) {
                throw new LlmServiceException("AI response missing criteria array");
            }

            List<String> criteria = new ArrayList<>();
            for (JsonNode item : criteriaNode) {
                criteria.add(item.asText());
            }
            return criteria;

        } catch (LlmServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to generate acceptance criteria via AI", e);
            throw new LlmServiceException("AI failed to generate acceptance criteria. Please try again.", e);
        }
    }

    // ─── Generate AC for Existing Story ────────────────────────────

    /**
     * Generate acceptance criteria for an existing story in DB.
     * If the story already has ACs, they are included as context for better AI output.
     * Returns a preview (list of strings) — does NOT save.
     */
    @Transactional(readOnly = true)
    public List<String> generateAcceptanceCriteriaForExistingStory(UUID userStoryId) {
        UserStory story = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));

        List<AcceptanceCriteria> existingAcs = story.getAcceptanceCriteria();

        String systemPrompt = """
                You are a senior QA engineer and business analyst. Generate acceptance criteria \
                for the given user story following the Given/When/Then format. Each criterion must be:
                1. Specific and measurable (no vague terms like "appropriate", "proper")
                2. Independent (testable on its own)
                3. Cover both happy path and edge cases
                4. Include validation rules where applicable
                
                If existing acceptance criteria are provided, analyze them and generate IMPROVED \
                versions that are more comprehensive and testable. Do not simply rephrase — add missing \
                scenarios and refine existing ones.
                
                Return ONLY a valid JSON object with key "criteria" containing an array of strings. \
                Do not add any extra text or explanation outside the JSON.""";

        StringBuilder userPromptBuilder = new StringBuilder();
        userPromptBuilder.append(String.format("""
                Generate acceptance criteria for this user story:
                
                Title: %s
                As a: %s
                I want to: %s
                So that: %s
                Description: %s
                """,
                nullSafe(story.getTitle()),
                nullSafe(story.getAsA()),
                nullSafe(story.getIWantTo()),
                nullSafe(story.getSoThat()),
                nullSafe(story.getDescription())));

        // Include existing ACs as context for smarter regeneration
        if (existingAcs != null && !existingAcs.isEmpty()) {
            userPromptBuilder.append("\nExisting Acceptance Criteria (improve and expand upon these):\n");
            for (int i = 0; i < existingAcs.size(); i++) {
                userPromptBuilder.append(String.format("%d. %s\n", i + 1, existingAcs.get(i).getContent()));
            }
        }

        userPromptBuilder.append("\nGenerate 4-6 comprehensive, testable acceptance criteria.");
        userPromptBuilder.append("\nReturn: {\"criteria\":[\"Given... When... Then...\",\"...\"]}");

        List<ChatMessage> messages = List.of(
                ChatMessage.builder().role("system").content(systemPrompt).build(),
                ChatMessage.builder().role("user").content(userPromptBuilder.toString()).build()
        );

        try {
            String response = llmProvider.chatCompletion(messages, 0.3, 1500, true);
            String json = extractJson(response);
            JsonNode node = objectMapper.readTree(json);

            JsonNode criteriaNode = node.has("criteria") ? node.get("criteria") : node;
            if (!criteriaNode.isArray()) {
                throw new LlmServiceException("AI response missing criteria array");
            }

            List<String> criteria = new ArrayList<>();
            for (JsonNode item : criteriaNode) {
                criteria.add(item.asText());
            }
            return criteria;

        } catch (LlmServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to generate acceptance criteria for existing story via AI", e);
            throw new LlmServiceException("AI failed to generate acceptance criteria. Please try again.", e);
        }
    }

    // ─── Refine User Story ───────────────────────────────────────────

    /**
     * Ask AI to refine a user story's fields. Returns a preview — does NOT save.
     */
    public RefinedUserStoryResponse refineUserStory(UUID userStoryId) {
        UserStory story = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));

        String systemPrompt = """
                You are a senior QA engineer and technical writer. Your task is to rewrite user story fields \
                to be clearer, more specific, testable, and professionally worded. Keep the original intent intact. \
                Return ONLY a valid JSON object with these exact keys: title, asA, iWantTo, soThat, description. \
                Do not add any extra text or explanation outside the JSON.""";

        String userPrompt = String.format("""
                Rewrite this user story to be more professional and testable:
                
                Title: %s
                As a: %s
                I want to: %s
                So that: %s
                Description: %s
                
                Return: {"title":"...","asA":"...","iWantTo":"...","soThat":"...","description":"..."}""",
                nullSafe(story.getTitle()),
                nullSafe(story.getAsA()),
                nullSafe(story.getIWantTo()),
                nullSafe(story.getSoThat()),
                nullSafe(story.getDescription()));

        List<ChatMessage> messages = List.of(
                ChatMessage.builder().role("system").content(systemPrompt).build(),
                ChatMessage.builder().role("user").content(userPrompt).build()
        );

        try {
            String response = llmProvider.chatCompletion(messages, 0.3, 512, true);
            String json = extractJson(response);
            JsonNode node = objectMapper.readTree(json);

            return RefinedUserStoryResponse.builder()
                    .title(getTextOrNull(node, "title"))
                    .asA(getTextOrNull(node, "asA"))
                    .iWantTo(getTextOrNull(node, "iWantTo"))
                    .soThat(getTextOrNull(node, "soThat"))
                    .description(getTextOrNull(node, "description"))
                    .build();

        } catch (LlmServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse AI refine response", e);
            throw new LlmServiceException("AI returned an invalid response. Please try again.", e);
        }
    }

    // ─── Generate Test Cases ────────────────────────────────────────

    /**
     * Generate test cases from a user story + acceptance criteria. Saves to DB.
     */
    @Transactional
    public GenerateTestCasesResponse generateTestCases(UUID userStoryId, List<String> requestedTypes) {
        UserStory story = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));

        List<AcceptanceCriteria> acList = story.getAcceptanceCriteria();
        if (acList == null || acList.isEmpty()) {
            throw new IllegalArgumentException("Story must have at least 1 acceptance criterion to generate test cases.");
        }

        // Default types
        List<String> types = (requestedTypes != null && !requestedTypes.isEmpty())
                ? requestedTypes
                : List.of("Positive", "Negative", "Boundary");

        String typesStr = String.join(", ", types);

        // Build AC list string
        StringBuilder acBuilder = new StringBuilder();
        for (int i = 0; i < acList.size(); i++) {
            acBuilder.append(String.format("%d. %s\n", i + 1, acList.get(i).getContent()));
        }

        String systemPrompt = """
                You are a senior QA engineer. Generate comprehensive test cases for the given user story and its acceptance criteria. \
                Each test case must be linked to a specific acceptance criterion by its index (1-based). \
                Each test case must have: acceptanceCriteriaIndex (integer), type (string), title (string), preconditions (string), steps (string with numbered steps), expectedResult (string). \
                Return ONLY a valid JSON object with key "testCases" containing an array of test case objects. \
                Do not add any extra text or explanation outside the JSON.""";

        String userPrompt = String.format("""
                Generate %s test cases for this user story:
                
                Title: %s
                As a: %s
                I want to: %s
                So that: %s
                Description: %s
                
                Acceptance Criteria:
                %s
                Return: {"testCases":[{"acceptanceCriteriaIndex":1,"type":"Positive","title":"...","preconditions":"...","steps":"1. ...","expectedResult":"..."}]}""",
                typesStr,
                nullSafe(story.getTitle()),
                nullSafe(story.getAsA()),
                nullSafe(story.getIWantTo()),
                nullSafe(story.getSoThat()),
                nullSafe(story.getDescription()),
                acBuilder.toString());

        List<ChatMessage> messages = List.of(
                ChatMessage.builder().role("system").content(systemPrompt).build(),
                ChatMessage.builder().role("user").content(userPrompt).build()
        );

        try {
            String response = llmProvider.chatCompletion(messages, 0.4, 3000, true);
            String json = extractJson(response);
            JsonNode rootNode = objectMapper.readTree(json);

            // Extract test cases array — handle both {"testCases":[...]} and direct [...]
            JsonNode testCasesNode;
            if (rootNode.has("testCases")) {
                testCasesNode = rootNode.get("testCases");
            } else if (rootNode.has("test_cases")) {
                testCasesNode = rootNode.get("test_cases");
            } else if (rootNode.isArray()) {
                testCasesNode = rootNode;
            } else {
                throw new LlmServiceException("AI response missing testCases array");
            }

            List<TestCase> savedTestCases = new ArrayList<>();

            for (JsonNode tcNode : testCasesNode) {
                int acIndex = tcNode.has("acceptanceCriteriaIndex")
                        ? tcNode.get("acceptanceCriteriaIndex").asInt(1)
                        : (tcNode.has("acceptance_criteria_index")
                            ? tcNode.get("acceptance_criteria_index").asInt(1) : 1);

                // Map AC index (1-based) to actual AC entity
                AcceptanceCriteria linkedAc = (acIndex >= 1 && acIndex <= acList.size())
                        ? acList.get(acIndex - 1)
                        : acList.get(0);

                TestCase testCase = TestCase.builder()
                        .userStory(story)
                        .acceptanceCriteria(linkedAc)
                        .title(getTextOrDefault(tcNode, "title", "Untitled Test Case"))
                        .preconditions(getTextOrNull(tcNode, "preconditions"))
                        .steps(getTextOrNull(tcNode, "steps"))
                        .expectedResult(getTextOrDefault(tcNode, "expectedResult",
                                getTextOrNull(tcNode, "expected_result")))
                        .generatedByAi(true)
                        .customFieldsJson("{}")
                        .build();

                TestCase saved = testCaseRepository.save(testCase);
                savedTestCases.add(saved);
            }

            // Publish WebSocket event
            eventPublisher.publishEvent(new EntityChangedEvent(
                    this,
                    EntityChangedEvent.EntityType.TEST_CASE,
                    EntityChangedEvent.Action.CREATED,
                    userStoryId.toString(),
                    story.getProject().getProjectId().toString(),
                    null,
                    "AI"
            ));

            // Build response
            List<GenerateTestCasesResponse.GeneratedTestCase> responseTcs = savedTestCases.stream()
                    .map(tc -> GenerateTestCasesResponse.GeneratedTestCase.builder()
                            .testCaseId(tc.getTestCaseId())
                            .title(tc.getTitle())
                            .type("AI")
                            .preconditions(tc.getPreconditions())
                            .steps(tc.getSteps())
                            .expectedResult(tc.getExpectedResult())
                            .acceptanceCriteriaId(tc.getAcceptanceCriteria() != null
                                    ? tc.getAcceptanceCriteria().getAcceptanceCriteriaId() : null)
                            .build())
                    .collect(Collectors.toList());

            return GenerateTestCasesResponse.builder()
                    .generatedCount(savedTestCases.size())
                    .testCases(responseTcs)
                    .build();

        } catch (LlmServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to generate test cases via AI", e);
            throw new LlmServiceException("AI failed to generate test cases. Please try again.", e);
        }
    }

    // ─── Helpers ────────────────────────────────────────────────────

    private String nullSafe(String s) {
        return s != null ? s : "(not provided)";
    }

    private String getTextOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }

    private String getTextOrDefault(JsonNode node, String field, String defaultValue) {
        String val = getTextOrNull(node, field);
        return val != null ? val : defaultValue;
    }

    /**
     * Extract JSON from LLM response — handles cases where model adds text around JSON.
     */
    private String extractJson(String response) {
        if (response == null || response.isBlank()) {
            throw new LlmServiceException("AI returned empty response");
        }

        String trimmed = response.trim();

        // Remove markdown code fences if present
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        trimmed = trimmed.trim();

        // Already valid JSON?
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) ||
            (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return trimmed;
        }

        // Try to extract JSON from surrounding text
        int braceStart = trimmed.indexOf('{');
        int bracketStart = trimmed.indexOf('[');

        if (braceStart >= 0) {
            int end = trimmed.lastIndexOf('}');
            if (end > braceStart) {
                return trimmed.substring(braceStart, end + 1);
            }
        }
        if (bracketStart >= 0) {
            int end = trimmed.lastIndexOf(']');
            if (end > bracketStart) {
                return trimmed.substring(bracketStart, end + 1);
            }
        }

        throw new LlmServiceException("AI response did not contain valid JSON: " + trimmed.substring(0, Math.min(200, trimmed.length())));
    }
}
