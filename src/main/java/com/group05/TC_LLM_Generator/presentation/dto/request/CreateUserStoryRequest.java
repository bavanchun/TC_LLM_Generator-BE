package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a new user story
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserStoryRequest {

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;

    private String description;

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;
}
