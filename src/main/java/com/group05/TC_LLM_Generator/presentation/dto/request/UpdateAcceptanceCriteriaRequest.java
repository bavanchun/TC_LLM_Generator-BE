package com.group05.TC_LLM_Generator.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing acceptance criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAcceptanceCriteriaRequest {

    private String content;

    private Integer orderNo;

    private Boolean completed;
}
