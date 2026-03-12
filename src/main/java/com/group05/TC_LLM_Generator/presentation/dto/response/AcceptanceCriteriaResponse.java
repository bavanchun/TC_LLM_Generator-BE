package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for AcceptanceCriteria entity with HATEOAS support
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptanceCriteriaResponse extends RepresentationModel<AcceptanceCriteriaResponse> {

    private UUID acceptanceCriteriaId;
    private UUID userStoryId;
    private String content;
    private Integer orderNo;
    private Boolean completed;
    private Instant createdAt;
}
