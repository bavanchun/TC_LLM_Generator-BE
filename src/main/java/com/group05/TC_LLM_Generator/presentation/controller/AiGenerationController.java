package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.AiGenerationService;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.GenerateAcceptanceCriteriaRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.GenerateTestCasesRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.GenerateTestCasesResponse;
import com.group05.TC_LLM_Generator.presentation.dto.response.RefinedUserStoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for AI-powered features:
 * - Refine user story
 * - Generate test cases
 * - Generate acceptance criteria
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AiGenerationController {

    private final AiGenerationService aiGenerationService;

    /**
     * POST /api/v1/user-stories/{id}/refine
     * Returns a preview of the refined user story (does NOT save).
     */
    @PostMapping("/api/v1/user-stories/{userStoryId}/refine")
    public ResponseEntity<ApiResponse<RefinedUserStoryResponse>> refineUserStory(
            @PathVariable UUID userStoryId) {
        log.info("Refine user story requested: {}", userStoryId);
        RefinedUserStoryResponse result = aiGenerationService.refineUserStory(userStoryId);
        return ResponseEntity.ok(ApiResponse.success(result, "Story refined successfully"));
    }

    /**
     * POST /api/v1/user-stories/{id}/generate-test-cases
     * Generates test cases via AI, saves them, and returns the result.
     */
    @PostMapping("/api/v1/user-stories/{userStoryId}/generate-test-cases")
    public ResponseEntity<ApiResponse<GenerateTestCasesResponse>> generateTestCases(
            @PathVariable UUID userStoryId,
            @RequestBody(required = false) GenerateTestCasesRequest request) {
        log.info("Generate test cases requested for story: {}", userStoryId);

        var types = request != null ? request.getTestCaseTypes() : null;
        GenerateTestCasesResponse result = aiGenerationService.generateTestCases(userStoryId, types);
        return ResponseEntity.ok(ApiResponse.success(result,
                String.format("Generated %d test cases successfully", result.getGeneratedCount())));
    }

    /**
     * POST /api/v1/ai/generate-acceptance-criteria
     * Generates acceptance criteria suggestions from story text fields (pre-save).
     */
    @PostMapping("/api/v1/ai/generate-acceptance-criteria")
    public ResponseEntity<ApiResponse<List<String>>> generateAcceptanceCriteria(
            @RequestBody GenerateAcceptanceCriteriaRequest request) {
        log.info("Generate acceptance criteria requested for title: {}", request.getTitle());

        List<String> criteria = aiGenerationService.generateAcceptanceCriteria(
                request.getTitle(),
                request.getAsA(),
                request.getIWantTo(),
                request.getSoThat(),
                request.getDescription()
        );

        return ResponseEntity.ok(ApiResponse.success(criteria,
                String.format("Generated %d acceptance criteria", criteria.size())));
    }
}
