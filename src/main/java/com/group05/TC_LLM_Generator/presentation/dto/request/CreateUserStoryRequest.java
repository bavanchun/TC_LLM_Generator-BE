package com.group05.TC_LLM_Generator.presentation.dto.request;

import com.group05.TC_LLM_Generator.domain.model.enums.StoryStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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

    @Size(max = 500, message = "As a must not exceed 500 characters")
    private String asA;

    private String iWantTo;

    private String soThat;

    @NotNull(message = "Status is required")
    private StoryStatus status;

    @Valid
    @Builder.Default
    private List<CreateAcceptanceCriteriaRequest> acceptanceCriteria = new ArrayList<>();
}
