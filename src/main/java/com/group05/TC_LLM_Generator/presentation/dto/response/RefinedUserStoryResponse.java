package com.group05.TC_LLM_Generator.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for refined user story preview
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefinedUserStoryResponse {
    private String title;
    private String asA;
    private String iWantTo;
    private String soThat;
    private String description;
}
