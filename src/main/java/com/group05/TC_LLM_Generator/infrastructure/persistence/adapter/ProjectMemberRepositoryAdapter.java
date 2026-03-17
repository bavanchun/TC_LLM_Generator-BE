package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.ProjectMemberRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectMemberRepositoryAdapter implements ProjectMemberRepositoryPort {

    private final ProjectMemberRepository jpaRepository;

    @Override
    public ProjectMember save(ProjectMember projectMember) {
        return jpaRepository.save(projectMember);
    }

    @Override
    public Optional<ProjectMember> findById(UUID projectMemberId) {
        return jpaRepository.findById(projectMemberId);
    }

    @Override
    public List<ProjectMember> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<ProjectMember> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public List<ProjectMember> findByProjectId(UUID projectId) {
        return jpaRepository.findByProject_ProjectId(projectId);
    }

    @Override
    public Page<ProjectMember> findByProjectId(UUID projectId, Pageable pageable) {
        return jpaRepository.findByProject_ProjectId(projectId, pageable);
    }

    @Override
    public Optional<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId) {
        return jpaRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId);
    }

    @Override
    public void deleteById(UUID projectMemberId) {
        jpaRepository.deleteById(projectMemberId);
    }

    @Override
    public boolean existsById(UUID projectMemberId) {
        return jpaRepository.existsById(projectMemberId);
    }

    @Override
    public List<ProjectMember> findByUserIdAndWorkspaceId(UUID userId, UUID workspaceId) {
        return jpaRepository.findByUser_UserIdAndProject_Workspace_WorkspaceId(userId, workspaceId);
    }
}
