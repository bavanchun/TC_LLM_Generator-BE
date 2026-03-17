package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceMemberService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceService;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import com.group05.TC_LLM_Generator.presentation.assembler.WorkspaceMemberModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.AddWorkspaceMemberRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateWorkspaceMemberRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.WorkspaceMemberResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
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

import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspace-members")
@RequiredArgsConstructor
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;
    private final WorkspaceService workspaceService;
    private final UserService userService;
    private final WorkspaceMemberModelAssembler assembler;
    private final PagedResourcesAssembler<WorkspaceMember> pagedResourcesAssembler;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> addWorkspaceMember(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddWorkspaceMemberRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        // Only Owner/Admin can add members
        if (!workspaceMemberService.isOwnerOrAdmin(request.getWorkspaceId(), currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only workspace Owner or Admin can add members"));
        }

        Workspace workspace = workspaceService.getWorkspaceById(request.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", request.getWorkspaceId()));

        UserEntity user = userService.getUserById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        WorkspaceMember savedMember = workspaceMemberService.addMember(workspace, user, request.getRole());
        WorkspaceMemberResponse response = assembler.toModel(savedMember);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityChangedEvent.EntityType.WORKSPACE_MEMBER,
                EntityChangedEvent.Action.CREATED,
                savedMember.getWorkspaceMemberId().toString(),
                request.getWorkspaceId().toString(),
                response, currentUserId.toString()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Workspace member added successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> getWorkspaceMemberById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        WorkspaceMember member = workspaceMemberService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkspaceMember", "id", id));

        // Only workspace members can view member details
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        if (!workspaceMemberService.isMember(member.getWorkspace().getWorkspaceId(), currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You are not a member of this workspace"));
        }

        WorkspaceMemberResponse response = assembler.toModel(member);
        return ResponseEntity.ok(ApiResponse.success(response, "Workspace member retrieved successfully"));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<PagedModel<WorkspaceMemberResponse>>> getWorkspaceMembersByWorkspace(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("workspaceId") UUID workspaceId,
            @PageableDefault(size = 20, sort = "joinedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // Only workspace members can list members
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        if (!workspaceMemberService.isMember(workspaceId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You are not a member of this workspace"));
        }

        Page<WorkspaceMember> page = workspaceMemberService.getByWorkspaceId(workspaceId, pageable);
        PagedModel<WorkspaceMemberResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Workspace members retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> updateWorkspaceMemberRole(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateWorkspaceMemberRequest request) {

        WorkspaceMember targetMember = workspaceMemberService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkspaceMember", "id", id));

        // Only Owner/Admin can change roles
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        if (!workspaceMemberService.isOwnerOrAdmin(targetMember.getWorkspace().getWorkspaceId(), currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only workspace Owner or Admin can change member roles"));
        }

        WorkspaceMember updated = workspaceMemberService.updateRole(id, request.getRole());
        WorkspaceMemberResponse response = assembler.toModel(updated);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityChangedEvent.EntityType.WORKSPACE_MEMBER,
                EntityChangedEvent.Action.UPDATED,
                id.toString(),
                targetMember.getWorkspace().getWorkspaceId().toString(),
                response, currentUserId.toString()));

        return ResponseEntity.ok(ApiResponse.success(response, "Workspace member updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeWorkspaceMember(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        WorkspaceMember targetMember = workspaceMemberService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkspaceMember", "id", id));

        // Only Owner/Admin can remove members
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        if (!workspaceMemberService.isOwnerOrAdmin(targetMember.getWorkspace().getWorkspaceId(), currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only workspace Owner or Admin can remove members"));
        }

        String workspaceId = targetMember.getWorkspace().getWorkspaceId().toString();
        workspaceMemberService.removeMember(id);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityChangedEvent.EntityType.WORKSPACE_MEMBER,
                EntityChangedEvent.Action.DELETED,
                id.toString(), workspaceId,
                null, currentUserId.toString()));

        return ResponseEntity.ok(ApiResponse.success("Workspace member removed successfully"));
    }
}
