package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @EntityGraph(attributePaths = {"workspace", "createdByUser"})
    Optional<Project> findByProjectKey(String projectKey);

    @EntityGraph(attributePaths = {"workspace", "createdByUser"})
    Optional<Project> findById(UUID id);

    @EntityGraph(attributePaths = {"workspace", "createdByUser"})
    List<Project> findByWorkspace_WorkspaceId(UUID workspaceId);

    @EntityGraph(attributePaths = {"workspace", "createdByUser"})
    Page<Project> findByWorkspace_WorkspaceId(UUID workspaceId, Pageable pageable);

    List<Project> findByStatus(String status);

    List<Project> findByWorkspace_WorkspaceIdAndStatus(UUID workspaceId, String status);

    @EntityGraph(attributePaths = {"workspace", "createdByUser"})
    List<Project> findByCreatedByUser_UserId(UUID userId);

    @EntityGraph(attributePaths = {"workspace", "createdByUser"})
    Page<Project> findByCreatedByUser_UserId(UUID userId, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Project p " +
            "JOIN FETCH p.workspace w " +
            "JOIN FETCH p.createdByUser " +
            "LEFT JOIN ProjectMember pm ON pm.project = p AND pm.user.userId = :userId " +
            "LEFT JOIN WorkspaceMember wm ON wm.workspace = w AND wm.user.userId = :userId " +
            "WHERE p.createdByUser.userId = :userId " +
            "OR w.ownerUser.userId = :userId " +
            "OR pm.user.userId = :userId " +
            "OR wm.user.userId = :userId",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Project p " +
            "JOIN p.workspace w " +
            "LEFT JOIN ProjectMember pm ON pm.project = p AND pm.user.userId = :userId " +
            "LEFT JOIN WorkspaceMember wm ON wm.workspace = w AND wm.user.userId = :userId " +
            "WHERE p.createdByUser.userId = :userId " +
            "OR w.ownerUser.userId = :userId " +
            "OR pm.user.userId = :userId " +
            "OR wm.user.userId = :userId")
    Page<Project> findAccessibleByUser(@Param("userId") UUID userId, Pageable pageable);

    boolean existsByProjectKey(String projectKey);

    boolean existsByWorkspace_WorkspaceIdAndProjectKey(UUID workspaceId, String projectKey);

    List<Project> findByJiraSiteId(String jiraSiteId);

    long countByWorkspace_WorkspaceId(UUID workspaceId);
}
