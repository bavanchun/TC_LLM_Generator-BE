package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for quick status-only update on a test plan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTestPlanStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;
}
