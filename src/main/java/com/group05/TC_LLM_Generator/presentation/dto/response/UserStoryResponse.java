package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for UserStory entity with HATEOAS support
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStoryResponse extends RepresentationModel<UserStoryResponse> {

    private UUID userStoryId;
    private UUID projectId;
    private String projectName;
    private String jiraIssueKey;
    private String jiraIssueId;
    private String title;
    private String description;
    private String status;
    private Instant createdAt;
}
