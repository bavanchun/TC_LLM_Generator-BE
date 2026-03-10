package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.presentation.assembler.WorkspaceModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateWorkspaceRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateWorkspaceRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.WorkspaceResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.WorkspacePresentationMapper;
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
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final UserService userService;
    private final WorkspacePresentationMapper mapper;
    private final WorkspaceModelAssembler assembler;
    private final PagedResourcesAssembler<Workspace> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateWorkspaceRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());
        UserEntity owner = userService.getUserById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        Workspace workspace = Workspace.builder()
                .ownerUser(owner)
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Workspace savedWorkspace = workspaceService.createWorkspace(workspace);
        WorkspaceResponse response = assembler.toModel(savedWorkspace);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Workspace created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> getWorkspaceById(@PathVariable("id") UUID id) {
        Workspace workspace = workspaceService.getWorkspaceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", id));

        WorkspaceResponse response = assembler.toModel(workspace);
        return ResponseEntity.ok(ApiResponse.success(response, "Workspace retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<WorkspaceResponse>>> getMyWorkspaces(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Page<Workspace> page = workspaceService.getAccessibleWorkspaces(currentUserId, pageable);
        PagedModel<WorkspaceResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Workspaces retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> updateWorkspace(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateWorkspaceRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Workspace existingWorkspace = workspaceService.getWorkspaceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", id));

        if (!existingWorkspace.getOwnerUser().getUserId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only the workspace owner can update this workspace"));
        }

        mapper.updateEntity(request, existingWorkspace);
        Workspace updatedWorkspace = workspaceService.updateWorkspace(id, existingWorkspace);
        WorkspaceResponse response = assembler.toModel(updatedWorkspace);

        return ResponseEntity.ok(ApiResponse.success(response, "Workspace updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkspace(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Workspace workspace = workspaceService.getWorkspaceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", id));

        if (!workspace.getOwnerUser().getUserId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only the workspace owner can delete this workspace"));
        }

        workspaceService.deleteWorkspace(id);
        return ResponseEntity.ok(ApiResponse.success("Workspace deleted successfully"));
    }

    @GetMapping("/owner/{userId}")
    public ResponseEntity<ApiResponse<PagedModel<WorkspaceResponse>>> getWorkspacesByOwner(
            @PathVariable("userId") UUID userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Workspace> page = workspaceService.getWorkspacesByOwner(userId, pageable);
        PagedModel<WorkspaceResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Workspaces retrieved successfully"));
    }
}
