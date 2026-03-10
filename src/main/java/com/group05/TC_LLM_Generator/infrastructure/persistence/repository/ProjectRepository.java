package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findByProjectKey(String projectKey);

    List<Project> findByWorkspace_WorkspaceId(UUID workspaceId);

    Page<Project> findByWorkspace_WorkspaceId(UUID workspaceId, Pageable pageable);

    List<Project> findByStatus(String status);

    List<Project> findByWorkspace_WorkspaceIdAndStatus(UUID workspaceId, String status);

    List<Project> findByCreatedByUser_UserId(UUID userId);

    Page<Project> findByCreatedByUser_UserId(UUID userId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN ProjectMember pm ON pm.project = p AND pm.user.userId = :userId " +
            "WHERE p.createdByUser.userId = :userId " +
            "OR p.workspace.ownerUser.userId = :userId " +
            "OR pm.user.userId = :userId")
    Page<Project> findAccessibleByUser(@Param("userId") UUID userId, Pageable pageable);

    boolean existsByProjectKey(String projectKey);

    List<Project> findByJiraSiteId(String jiraSiteId);
}
