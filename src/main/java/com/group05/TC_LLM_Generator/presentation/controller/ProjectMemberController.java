package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.ProjectAuthorizationService;
import com.group05.TC_LLM_Generator.application.service.ProjectMemberService;
import com.group05.TC_LLM_Generator.application.service.ProjectService;
import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceMemberService;
import com.group05.TC_LLM_Generator.domain.model.enums.ProjectRole;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.presentation.assembler.ProjectMemberModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.AddProjectMemberRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateProjectMemberRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.ProjectMemberResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.ProjectMemberPresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/project-members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;
    private final ProjectService projectService;
    private final UserService userService;
    private final WorkspaceMemberService workspaceMemberService;
    private final ProjectAuthorizationService projectAuth;
    private final ProjectMemberPresentationMapper mapper;
    private final ProjectMemberModelAssembler assembler;
    private final PagedResourcesAssembler<ProjectMember> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> addProjectMember(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddProjectMemberRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        Project project = projectService.getProjectById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        // Auth: only Lead or WS Admin/Owner can add project members
        UUID workspaceId = project.getWorkspace().getWorkspaceId();
        projectAuth.requireLeadOrAdminAccess(project.getProjectId(), workspaceId, currentUserId);

        // Validate: user being added must be a workspace member
        if (!workspaceMemberService.isMember(workspaceId, request.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("User must be a workspace member before being added to a project"));
        }

        UserEntity user = userService.getUserById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        ProjectMember savedMember = projectMemberService.addMember(project, user, request.getRole());
        ProjectMemberResponse response = assembler.toModel(savedMember);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Project member added successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> getProjectMemberById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        ProjectMember member = projectMemberService.getProjectMemberById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMember", "id", id));

        // Auth: only Lead or WS Admin/Owner can view team details
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        UUID workspaceId = member.getProject().getWorkspace().getWorkspaceId();
        projectAuth.requireLeadOrAdminAccess(member.getProject().getProjectId(), workspaceId, currentUserId);

        ProjectMemberResponse response = assembler.toModel(member);
        return ResponseEntity.ok(ApiResponse.success(response, "Project member retrieved successfully"));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<PagedModel<ProjectMemberResponse>>> getProjectMembersByProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("projectId") UUID projectId,
            @PageableDefault(size = 20, sort = "joinedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        // Auth: only Lead or WS Admin/Owner can view team page
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        UUID workspaceId = project.getWorkspace().getWorkspaceId();
        projectAuth.requireLeadOrAdminAccess(projectId, workspaceId, currentUserId);

        Page<ProjectMember> page = projectMemberService.getProjectMembersByProject(projectId, pageable);
        PagedModel<ProjectMemberResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Project members retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> updateProjectMemberRole(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateProjectMemberRequest request) {

        ProjectMember existing = projectMemberService.getProjectMemberById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMember", "id", id));

        // Auth: only Lead or WS Admin/Owner can change roles
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        UUID workspaceId = existing.getProject().getWorkspace().getWorkspaceId();
        projectAuth.requireLeadOrAdminAccess(existing.getProject().getProjectId(), workspaceId, currentUserId);

        mapper.updateEntity(request, existing);
        ProjectMember updated = projectMemberService.updateProjectMember(id, existing);
        ProjectMemberResponse response = assembler.toModel(updated);

        return ResponseEntity.ok(ApiResponse.success(response, "Project member updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeProjectMember(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        ProjectMember member = projectMemberService.getProjectMemberById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMember", "id", id));

        // Auth: only Lead or WS Admin/Owner can remove members
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        UUID workspaceId = member.getProject().getWorkspace().getWorkspaceId();
        projectAuth.requireLeadOrAdminAccess(member.getProject().getProjectId(), workspaceId, currentUserId);

        projectMemberService.removeMember(id);
        return ResponseEntity.ok(ApiResponse.success("Project member removed successfully"));
    }

    /**
     * Check if the current user can manage this project's team.
     * Returns { canManageTeam: true/false, projectRole: "..." }
     */
    @GetMapping("/project/{projectId}/my-role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyProjectRole(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("projectId") UUID projectId) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        UUID workspaceId = project.getWorkspace().getWorkspaceId();
        boolean isWsAdmin = workspaceMemberService.isOwnerOrAdmin(workspaceId, currentUserId);
        ProjectRole projectRole = projectAuth.getUserProjectRole(projectId, currentUserId);

        boolean canManageTeam = isWsAdmin || projectRole == ProjectRole.Lead;

        String roleStr = isWsAdmin ? "Admin" : (projectRole != null ? projectRole.name() : "None");

        Map<String, Object> result = Map.of(
                "canManageTeam", canManageTeam,
                "projectRole", roleStr,
                "isWorkspaceAdmin", isWsAdmin
        );

        return ResponseEntity.ok(ApiResponse.success(result, "Role retrieved"));
    }
}
