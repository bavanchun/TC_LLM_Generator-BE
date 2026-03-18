package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for ProjectMember repository operations.
 * Defines the contract for persistence operations on ProjectMember entities.
 */
public interface ProjectMemberRepositoryPort {

    ProjectMember save(ProjectMember projectMember);

    Optional<ProjectMember> findById(UUID projectMemberId);

    List<ProjectMember> findAll();

    Page<ProjectMember> findAll(Pageable pageable);

    List<ProjectMember> findByProjectId(UUID projectId);

    Page<ProjectMember> findByProjectId(UUID projectId, Pageable pageable);

    Optional<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId);

    void deleteById(UUID projectMemberId);

    boolean existsById(UUID projectMemberId);

    List<ProjectMember> findByUserIdAndWorkspaceId(UUID userId, UUID workspaceId);

    long countByProjectId(UUID projectId);
}
