package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for WorkspaceMember entity
 */
@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {

    /**
     * Find workspace members by workspace ID
     * @param workspaceId workspace ID
     * @return List of workspace members
     */
    List<WorkspaceMember> findByWorkspace_WorkspaceId(UUID workspaceId);

    /**
     * Find workspace members by user ID
     * @param userId user ID
     * @return List of workspace memberships
     */
    List<WorkspaceMember> findByUser_UserId(UUID userId);

    /**
     * Find workspace member by workspace ID and user ID
     * @param workspaceId workspace ID
     * @param userId user ID
     * @return Optional of WorkspaceMember
     */
    Optional<WorkspaceMember> findByWorkspace_WorkspaceIdAndUser_UserId(UUID workspaceId, UUID userId);

    /**
     * Find workspace members by role
     * @param workspaceId workspace ID
     * @param role member role
     * @return List of workspace members with the specified role
     */
    List<WorkspaceMember> findByWorkspace_WorkspaceIdAndRole(UUID workspaceId, String role);

    /**
     * Count members in a workspace
     * @param workspaceId workspace ID
     * @return number of members
     */
    long countByWorkspace_WorkspaceId(UUID workspaceId);
}
