package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceInvitationService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceMemberService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceInvitation;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import com.group05.TC_LLM_Generator.presentation.assembler.InvitationModelAssembler;
import com.group05.TC_LLM_Generator.presentation.assembler.WorkspaceMemberModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.SendInvitationRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.InvitationResponse;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/workspace-invitations")
@RequiredArgsConstructor
public class WorkspaceInvitationController {

    private final WorkspaceInvitationService invitationService;
    private final WorkspaceService workspaceService;
    private final UserService userService;
    private final WorkspaceMemberService workspaceMemberService;
    private final InvitationModelAssembler invitationAssembler;
    private final WorkspaceMemberModelAssembler memberAssembler;
    private final PagedResourcesAssembler<WorkspaceInvitation> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<InvitationResponse>> sendInvitation(
            @Valid @RequestBody SendInvitationRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID inviterUserId = UUID.fromString(jwt.getSubject());

        // Only Owner/Admin can send invitations
        if (!workspaceMemberService.isOwnerOrAdmin(request.getWorkspaceId(), inviterUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only workspace Owner or Admin can send invitations"));
        }

        UserEntity inviter = userService.getUserById(inviterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", inviterUserId));

        Workspace workspace = workspaceService.getWorkspaceById(request.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", request.getWorkspaceId()));

        WorkspaceInvitation invitation = invitationService.sendInvitation(
                workspace, inviter, request.getEmail(), request.getRole());
        InvitationResponse response = invitationAssembler.toModel(invitation);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Invitation sent successfully"));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<PagedModel<InvitationResponse>>> getInvitationsByWorkspace(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("workspaceId") UUID workspaceId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // Only workspace members can view invitations
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        if (!workspaceMemberService.isMember(workspaceId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You are not a member of this workspace"));
        }

        Page<WorkspaceInvitation> page = invitationService.getByWorkspaceId(workspaceId, pageable);
        PagedModel<InvitationResponse> pagedModel = pagedResourcesAssembler.toModel(page, invitationAssembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Invitations retrieved successfully"));
    }

    @GetMapping("/workspace/{workspaceId}/pending")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getPendingInvitations(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("workspaceId") UUID workspaceId) {

        // Only workspace members can see pending invitations
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        if (!workspaceMemberService.isMember(workspaceId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You are not a member of this workspace"));
        }

        List<InvitationResponse> responses = invitationService.getPendingInvitations(workspaceId)
                .stream()
                .map(invitationAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses, "Pending invitations retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelInvitation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        // JWT ensures only authenticated users can cancel
        // The service validates invitation status
        invitationService.cancelInvitation(id);
        return ResponseEntity.ok(ApiResponse.success("Invitation cancelled successfully"));
    }

    // Public endpoint — get invitation info by token (for accept page)
    @GetMapping("/{token}/info")
    public ResponseEntity<ApiResponse<InvitationResponse>> getInvitationInfo(
            @PathVariable("token") String token) {

        WorkspaceInvitation invitation = invitationService.getByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "token", token));

        InvitationResponse response = invitationAssembler.toModel(invitation);

        return ResponseEntity.ok(ApiResponse.success(response, "Invitation info retrieved successfully"));
    }

    // Accept invitation
    @PostMapping("/{token}/accept")
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> acceptInvitation(
            @PathVariable("token") String token,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());
        UserEntity user = userService.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        WorkspaceMember member = invitationService.acceptInvitation(token, user);
        WorkspaceMemberResponse response = memberAssembler.toModel(member);

        return ResponseEntity.ok(ApiResponse.success(response, "Invitation accepted, you are now a workspace member"));
    }
}
