package com.group05.TC_LLM_Generator.infrastructure.ai;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group05.TC_LLM_Generator.application.port.out.LLMProviderPort;
import com.group05.TC_LLM_Generator.infrastructure.ai.dto.ChatCompletionRequest;
import com.group05.TC_LLM_Generator.infrastructure.ai.dto.ChatCompletionResponse;
import com.group05.TC_LLM_Generator.infrastructure.ai.dto.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * RunPod vLLM adapter — calls the OpenAI-compatible chat/completions endpoint.
 */
@Slf4j
@Service
public class RunPodLlmAdapter implements LLMProviderPort {

    private final RestTemplate restTemplate;
    private final RunPodConfig config;
    private final ObjectMapper objectMapper;

    public RunPodLlmAdapter(
            @Qualifier("runpodRestTemplate") RestTemplate restTemplate,
            RunPodConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String chatCompletion(List<ChatMessage> messages, double temperature, int maxTokens, boolean jsonMode) {
        String url = config.getEndpointUrl() + "/chat/completions";

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(config.getModel())
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .stream(false)
                .responseFormat(jsonMode ? Map.of("type", "json_object") : null)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getApiKey());

        try {
            String requestBody = objectMapper.writeValueAsString(request);
            log.info("RunPod request to {} | model={} | temp={} | maxTokens={} | jsonMode={}",
                    url, config.getModel(), temperature, maxTokens, jsonMode);

            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url, HttpMethod.POST, httpEntity, String.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
                log.error("RunPod returned non-2xx: status={}, body={}", responseEntity.getStatusCode(), responseEntity.getBody());
                throw new RuntimeException("RunPod API returned status " + responseEntity.getStatusCode());
            }

            ChatCompletionResponse response = objectMapper.readValue(responseEntity.getBody(), ChatCompletionResponse.class);

            if (response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("RunPod returned empty choices");
            }

            String content = response.getChoices().get(0).getMessage().getContent();
            log.info("RunPod response received | usage: {}", response.getUsage());
            return content;

        } catch (Exception e) {
            log.error("RunPod call failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call RunPod LLM: " + e.getMessage(), e);
        }
    }
}
