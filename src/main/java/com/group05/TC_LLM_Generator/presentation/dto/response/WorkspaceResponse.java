package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for Workspace entity with HATEOAS support
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResponse extends RepresentationModel<WorkspaceResponse> {

    private UUID workspaceId;
    private UUID ownerUserId;
    private String ownerFullName;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    // Computed stats
    private long projectCount;
    private long memberCount;
}
