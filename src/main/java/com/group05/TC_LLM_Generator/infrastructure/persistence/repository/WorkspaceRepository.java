package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Workspace entity
 */
@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    /**
     * Find workspaces by owner user ID
     * 
     * @param ownerUserId owner user ID
     * @return List of workspaces
     */
    @EntityGraph(attributePaths = { "projects", "projects.members" })
    List<Workspace> findByOwnerUser_UserId(UUID ownerUserId);

    /**
     * Find workspace by name
     * 
     * @param name workspace name
     * @return List of workspaces with the specified name
     */
    List<Workspace> findByName(String name);

    /**
     * Find workspaces by name containing (case-insensitive search)
     * 
     * @param name search term
     * @return List of matching workspaces
     */
    List<Workspace> findByNameContainingIgnoreCase(String name);
}
