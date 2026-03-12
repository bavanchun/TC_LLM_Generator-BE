package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.ProjectService;
import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.presentation.assembler.ProjectModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateProjectRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateProjectRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.ProjectResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.ProjectPresentationMapper;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final WorkspaceService workspaceService;
    private final UserService userService;
    private final ProjectPresentationMapper mapper;
    private final ProjectModelAssembler assembler;
    private final PagedResourcesAssembler<Project> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateProjectRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        Workspace workspace = workspaceService.getWorkspaceById(request.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", request.getWorkspaceId()));

        UserEntity creator = userService.getUserById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        Project project = Project.builder()
                .workspace(workspace)
                .createdByUser(creator)
                .projectKey(request.getProjectKey())
                .name(request.getName())
                .description(request.getDescription())
                .jiraSiteId(request.getJiraSiteId())
                .jiraProjectKey(request.getJiraProjectKey())
                .status("ACTIVE")
                .build();

        Project savedProject = projectService.createProject(project);
        ProjectResponse response = assembler.toModel(savedProject);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Project created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable("id") UUID id) {
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        ProjectResponse response = assembler.toModel(project);
        return ResponseEntity.ok(ApiResponse.success(response, "Project retrieved successfully"));
    }

    @GetMapping("/key/{projectKey}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectByKey(@PathVariable("projectKey") String projectKey) {
        Project project = projectService.getProjectByKey(projectKey)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "key", projectKey));

        ProjectResponse response = assembler.toModel(project);
        return ResponseEntity.ok(ApiResponse.success(response, "Project retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<ProjectResponse>>> getMyProjects(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Page<Project> page = projectService.getAccessibleProjects(currentUserId, pageable);
        PagedModel<ProjectResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Projects retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateProjectRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project existingProject = projectService.getProjectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        if (!existingProject.getCreatedByUser().getUserId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only the project creator can update this project"));
        }

        mapper.updateEntity(request, existingProject);
        Project updatedProject = projectService.updateProject(id, existingProject, currentUserId.toString());
        ProjectResponse response = assembler.toModel(updatedProject);

        return ResponseEntity.ok(ApiResponse.success(response, "Project updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        if (!project.getCreatedByUser().getUserId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only the project creator can delete this project"));
        }

        projectService.deleteProject(id, currentUserId.toString());
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully"));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<PagedModel<ProjectResponse>>> getProjectsByWorkspace(
            @PathVariable("workspaceId") UUID workspaceId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Project> page = projectService.getProjectsByWorkspace(workspaceId, pageable);
        PagedModel<ProjectResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Projects retrieved successfully"));
    }
}
