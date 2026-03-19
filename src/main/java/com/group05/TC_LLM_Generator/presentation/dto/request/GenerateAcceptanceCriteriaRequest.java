package com.group05.TC_LLM_Generator.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for generating acceptance criteria from a story description
 * (before the story is persisted).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateAcceptanceCriteriaRequest {
    private String title;
    private String asA;
    private String iWantTo;
    private String soThat;
    private String description;
}
