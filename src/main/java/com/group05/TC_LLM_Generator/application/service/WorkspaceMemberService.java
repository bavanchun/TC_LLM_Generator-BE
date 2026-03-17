package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.ProjectMemberRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.WorkspaceMemberRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.WorkspaceRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceMemberService {

    private final WorkspaceMemberRepositoryPort workspaceMemberRepository;
    private final WorkspaceRepositoryPort workspaceRepository;
    private final ProjectMemberRepositoryPort projectMemberRepository;

    @Transactional
    public WorkspaceMember addMember(Workspace workspace, UserEntity user, String role) {
        // Check if already a member
        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                workspace.getWorkspaceId(), user.getUserId())) {
            throw new IllegalArgumentException("User is already a member of this workspace");
        }

        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(workspace)
                .user(user)
                .role(role)
                .joinedAt(Instant.now())
                .build();
        return workspaceMemberRepository.save(member);
    }

    public Optional<WorkspaceMember> getById(UUID workspaceMemberId) {
        return workspaceMemberRepository.findById(workspaceMemberId);
    }

    @Transactional
    public List<WorkspaceMember> getByWorkspaceId(UUID workspaceId) {
        ensureOwnerMember(workspaceId);
        return workspaceMemberRepository.findByWorkspaceId(workspaceId);
    }

    @Transactional
    public Page<WorkspaceMember> getByWorkspaceId(UUID workspaceId, Pageable pageable) {
        ensureOwnerMember(workspaceId);
        return workspaceMemberRepository.findByWorkspaceId(workspaceId, pageable);
    }

    public Optional<WorkspaceMember> getByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
    }

    public List<WorkspaceMember> getByUserId(UUID userId) {
        return workspaceMemberRepository.findByUserId(userId);
    }

    public boolean isMember(UUID workspaceId, UUID userId) {
        // Also check if user is the workspace owner
        Optional<Workspace> wsOpt = workspaceRepository.findById(workspaceId);
        if (wsOpt.isPresent() && wsOpt.get().getOwnerUser().getUserId().equals(userId)) {
            return true;
        }
        return workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }

    public boolean isOwnerOrAdmin(UUID workspaceId, UUID userId) {
        // Check if user is the workspace owner first
        Optional<Workspace> wsOpt = workspaceRepository.findById(workspaceId);
        if (wsOpt.isPresent() && wsOpt.get().getOwnerUser().getUserId().equals(userId)) {
            return true;
        }
        Optional<WorkspaceMember> member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, userId);
        return member.map(m -> "Owner".equals(m.getRole()) || "Admin".equals(m.getRole()))
                .orElse(false);
    }

    @Transactional
    public WorkspaceMember updateRole(UUID workspaceMemberId, String newRole) {
        WorkspaceMember member = workspaceMemberRepository.findById(workspaceMemberId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Workspace member not found: " + workspaceMemberId));

        if ("Owner".equals(member.getRole())) {
            throw new IllegalArgumentException("Cannot change the role of the workspace owner");
        }

        member.setRole(newRole);
        return workspaceMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(UUID workspaceMemberId) {
        WorkspaceMember member = workspaceMemberRepository.findById(workspaceMemberId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Workspace member not found: " + workspaceMemberId));

        if ("Owner".equals(member.getRole())) {
            throw new IllegalArgumentException("Cannot remove the workspace owner");
        }

        // Cascade — remove all ProjectMember records in projects that belong to this workspace
        UUID userId = member.getUser().getUserId();
        UUID workspaceId = member.getWorkspace().getWorkspaceId();
        List<ProjectMember> projectMemberships = projectMemberRepository.findAll();
        for (ProjectMember pm : projectMemberships) {
            if (pm.getUser().getUserId().equals(userId)
                    && pm.getProject().getWorkspace().getWorkspaceId().equals(workspaceId)) {
                projectMemberRepository.deleteById(pm.getProjectMemberId());
                log.info("Cascade-removed ProjectMember '{}' from project '{}'",
                        pm.getProjectMemberId(), pm.getProject().getName());
            }
        }

        workspaceMemberRepository.deleteById(workspaceMemberId);
    }

    public long countMembers(UUID workspaceId) {
        return workspaceMemberRepository.countByWorkspaceId(workspaceId);
    }

    /**
     * Self-healing: ensure the workspace owner has a WorkspaceMember record.
     * Workspaces created before the auto-create fix won't have one.
     */
    @Transactional
    public void ensureOwnerMember(UUID workspaceId) {
        Optional<Workspace> wsOpt = workspaceRepository.findById(workspaceId);
        if (wsOpt.isEmpty()) return;

        Workspace ws = wsOpt.get();
        UUID ownerUserId = ws.getOwnerUser().getUserId();

        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, ownerUserId)) {
            WorkspaceMember ownerMember = WorkspaceMember.builder()
                    .workspace(ws)
                    .user(ws.getOwnerUser())
                    .role("Owner")
                    .joinedAt(ws.getCreatedAt() != null ? ws.getCreatedAt() : Instant.now())
                    .build();
            workspaceMemberRepository.save(ownerMember);
            log.info("Auto-created Owner WorkspaceMember for workspace '{}' ({})",
                    ws.getName(), workspaceId);
        }
    }
}
