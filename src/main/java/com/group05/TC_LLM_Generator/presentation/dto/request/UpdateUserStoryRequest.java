package com.group05.TC_LLM_Generator.presentation.dto.request;

import com.group05.TC_LLM_Generator.domain.model.enums.StoryStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Size(max = 500, message = "As a must not exceed 500 characters")
    private String asA;

    private String iWantTo;

    private String soThat;

    private StoryStatus status;

    @Valid
    private List<CreateAcceptanceCriteriaRequest> acceptanceCriteria;
}
