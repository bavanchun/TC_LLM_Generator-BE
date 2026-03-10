package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberResponse extends RepresentationModel<ProjectMemberResponse> {

    private UUID projectMemberId;
    private UUID projectId;
    private String projectName;
    private UUID userId;
    private String userFullName;
    private String userEmail;
    private String role;
    private Instant joinedAt;
}
