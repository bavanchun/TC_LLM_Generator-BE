package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ProjectMember entity
 */
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {

    /**
     * Find project members by project ID
     * @param projectId project ID
     * @return List of project members
     */
    @EntityGraph(attributePaths = {"project", "user"})
    List<ProjectMember> findByProject_ProjectId(UUID projectId);

    /**
     * Find project members by user ID
     * @param userId user ID
     * @return List of project memberships
     */
    List<ProjectMember> findByUser_UserId(UUID userId);

    /**
     * Find project member by project ID and user ID
     * @param projectId project ID
     * @param userId user ID
     * @return Optional of ProjectMember
     */
    Optional<ProjectMember> findByProject_ProjectIdAndUser_UserId(UUID projectId, UUID userId);

    /**
     * Find project members by role
     * @param projectId project ID
     * @param role member role
     * @return List of project members with the specified role
     */
    List<ProjectMember> findByProject_ProjectIdAndRole(UUID projectId, String role);

    @EntityGraph(attributePaths = {"project", "project.workspace", "user"})
    Page<ProjectMember> findByProject_ProjectId(UUID projectId, Pageable pageable);

    List<ProjectMember> findByUser_UserIdAndProject_Workspace_WorkspaceId(UUID userId, UUID workspaceId);

    long countByProject_ProjectId(UUID projectId);
}

