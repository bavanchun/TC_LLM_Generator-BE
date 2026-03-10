package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
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
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    @EntityGraph(attributePaths = {"ownerUser"})
    List<Workspace> findByOwnerUser_UserId(UUID ownerUserId);

    @EntityGraph(attributePaths = {"ownerUser"})
    Page<Workspace> findByOwnerUser_UserId(UUID ownerUserId, Pageable pageable);

    @EntityGraph(attributePaths = {"ownerUser"})
    Optional<Workspace> findById(UUID id);

    @Query(value = "SELECT DISTINCT w FROM Workspace w " +
            "JOIN FETCH w.ownerUser " +
            "LEFT JOIN WorkspaceMember wm ON wm.workspace = w AND wm.user.userId = :userId " +
            "WHERE w.ownerUser.userId = :userId OR wm.user.userId = :userId",
            countQuery = "SELECT COUNT(DISTINCT w) FROM Workspace w " +
            "LEFT JOIN WorkspaceMember wm ON wm.workspace = w AND wm.user.userId = :userId " +
            "WHERE w.ownerUser.userId = :userId OR wm.user.userId = :userId")
    Page<Workspace> findAccessibleByUser(@Param("userId") UUID userId, Pageable pageable);

    List<Workspace> findByName(String name);

    List<Workspace> findByNameContainingIgnoreCase(String name);
}
