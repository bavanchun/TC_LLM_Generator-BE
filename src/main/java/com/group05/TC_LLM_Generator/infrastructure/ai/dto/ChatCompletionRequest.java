package com.group05.TC_LLM_Generator.infrastructure.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest {
    private String model;
    private List<ChatMessage> messages;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    private Double temperature;
    
    @JsonProperty("top_p")
    private Double topP;
    
    @Builder.Default
    private Boolean stream = false;

    @JsonProperty("response_format")
    private Map<String, String> responseFormat;
}
