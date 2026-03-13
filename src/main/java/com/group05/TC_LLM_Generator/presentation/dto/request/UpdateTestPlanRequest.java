package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for updating an existing test plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTestPlanRequest {

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;

    private String status;

    /** Optional — replace the linked user stories */
    private List<UUID> storyIds;
}
