package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing user story
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStoryRequest {

    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;

    private String description;

    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;
}
