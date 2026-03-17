package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.ProjectMemberRepositoryPort;
import com.group05.TC_LLM_Generator.domain.model.enums.ProjectRole;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Centralized authorization service for project-level operations.
 *
 * Role hierarchy:
 * - Lead:      full access (CRUD + manage)
 * - Developer: create + read + update (no delete)
 * - Tester:    create + read + update (no delete)
 * - Viewer:    read only
 *
 * Workspace Owner/Admin bypasses all project role checks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectAuthorizationService {

    private final ProjectMemberRepositoryPort projectMemberRepository;
    private final WorkspaceMemberService workspaceMemberService;

    private static final Set<ProjectRole> CAN_EDIT = Set.of(
            ProjectRole.Lead, ProjectRole.Developer, ProjectRole.Tester
    );

    /**
     * Require read access to the project.
     * Any project member, or workspace Owner/Admin can read.
     */
    public void requireProjectAccess(UUID projectId, UUID workspaceId, UUID userId) {
        // Workspace Owner/Admin bypass
        if (workspaceMemberService.isOwnerOrAdmin(workspaceId, userId)) {
            return;
        }
        // Must be a project member (any role)
        Optional<ProjectMember> member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        if (member.isEmpty()) {
            throw new AccessDeniedException(
                    "You do not have access to this project. Please ask a workspace admin to assign you.");
        }
    }

    /**
     * Require create/edit access.
     * Lead, Developer, Tester roles — or workspace Owner/Admin.
     */
    public void requireContributorAccess(UUID projectId, UUID workspaceId, UUID userId) {
        // Workspace Owner/Admin bypass
        if (workspaceMemberService.isOwnerOrAdmin(workspaceId, userId)) {
            return;
        }
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new AccessDeniedException(
                        "You do not have access to this project."));

        ProjectRole role = ProjectRole.fromString(member.getRole());
        if (!CAN_EDIT.contains(role)) {
            throw new AccessDeniedException(
                    "Your role (" + role + ") does not allow creating or editing in this project. Required: Lead, Developer, or Tester.");
        }
    }

    /**
     * Require delete/manage access.
     * Only Lead role — or workspace Owner/Admin.
     */
    public void requireLeadAccess(UUID projectId, UUID workspaceId, UUID userId) {
        // Workspace Owner/Admin bypass
        if (workspaceMemberService.isOwnerOrAdmin(workspaceId, userId)) {
            return;
        }
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new AccessDeniedException(
                        "You do not have access to this project."));

        ProjectRole role = ProjectRole.fromString(member.getRole());
        if (role != ProjectRole.Lead) {
            throw new AccessDeniedException(
                    "Your role (" + role + ") does not allow deleting in this project. Required: Lead.");
        }
    }

    /**
     * Require Lead or Workspace Admin/Owner access.
     * Used for team management operations: viewing team page, changing roles, adding/removing members.
     */
    public void requireLeadOrAdminAccess(UUID projectId, UUID workspaceId, UUID userId) {
        // Workspace Owner/Admin bypass
        if (workspaceMemberService.isOwnerOrAdmin(workspaceId, userId)) {
            return;
        }
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new AccessDeniedException(
                        "You do not have access to this project."));

        ProjectRole role = ProjectRole.fromString(member.getRole());
        if (role != ProjectRole.Lead) {
            throw new AccessDeniedException(
                    "Only project Leads and workspace Admins/Owners can manage team members.");
        }
    }

    /**
     * Check if user is Lead or Workspace Admin/Owner (non-throwing).
     */
    public boolean isLeadOrAdmin(UUID projectId, UUID workspaceId, UUID userId) {
        if (workspaceMemberService.isOwnerOrAdmin(workspaceId, userId)) {
            return true;
        }
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(m -> ProjectRole.fromString(m.getRole()) == ProjectRole.Lead)
                .orElse(false);
    }

    /**
     * Get the user's project role (or null if not a member).
     */
    public ProjectRole getUserProjectRole(UUID projectId, UUID userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(m -> ProjectRole.fromString(m.getRole()))
                .orElse(null);
    }
}
