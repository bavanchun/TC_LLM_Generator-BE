package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new acceptance criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAcceptanceCriteriaRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Order number is required")
    private Integer orderNo;

    @Builder.Default
    private Boolean completed = false;
}
