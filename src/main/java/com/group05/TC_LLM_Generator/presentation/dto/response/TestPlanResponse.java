package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for TestPlan entity with HATEOAS support
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestPlanResponse extends RepresentationModel<TestPlanResponse> {

    private UUID testPlanId;
    private UUID projectId;
    private UUID createdByUserId;
    private String createdByUserFullName;
    private String name;
    private String description;
    private String status;
    private List<UUID> storyIds;
    private Instant createdAt;
    private Instant updatedAt;
}
