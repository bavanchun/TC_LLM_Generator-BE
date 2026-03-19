package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.ai.dto.ChatMessage;
import java.util.List;

/**
 * Port interface for LLM providers (RunPod vLLM, OpenAI, etc.)
 * Synchronous — callers block until LLM responds (suitable for serverless cold-start scenarios).
 */
public interface LLMProviderPort {

    /**
     * Send chat completion request and return the assistant's response content.
     *
     * @param messages  The conversation messages (system + user)
     * @param temperature  Sampling temperature (0.0–1.0)
     * @param maxTokens  Maximum tokens to generate
     * @param jsonMode  If true, request JSON-only output
     * @return The assistant's response content string
     */
    String chatCompletion(List<ChatMessage> messages, double temperature, int maxTokens, boolean jsonMode);
}
