package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for Project repository operations.
 * Defines the contract for persistence operations on Project entities.
 */
public interface ProjectRepositoryPort {

    Project save(Project project);

    Optional<Project> findById(UUID projectId);

    Optional<Project> findByProjectKey(String projectKey);

    List<Project> findAll();

    Page<Project> findAll(Pageable pageable);

    List<Project> findByWorkspaceId(UUID workspaceId);

    Page<Project> findByWorkspaceId(UUID workspaceId, Pageable pageable);

    List<Project> findByCreatedByUserId(UUID userId);

    Page<Project> findByCreatedByUserId(UUID userId, Pageable pageable);

    Page<Project> findAccessibleByUser(UUID userId, Pageable pageable);

    void deleteById(UUID projectId);

    boolean existsById(UUID projectId);

    boolean existsByProjectKey(String projectKey);
}
