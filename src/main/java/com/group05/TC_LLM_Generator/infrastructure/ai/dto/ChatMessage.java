package com.group05.TC_LLM_Generator.infrastructure.ai.dto;

import lombok.*;

/**
 * DTO for chat message in OpenAI-compatible API format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    /**
     * Role: "system", "user", or "assistant"
     */
    private String role;
    
    /**
     * Message content/text
     */
    private String content;
}
