package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for Workspace repository operations.
 * Defines the contract for persistence operations on Workspace entities.
 */
public interface WorkspaceRepositoryPort {

    Workspace save(Workspace workspace);

    Optional<Workspace> findById(UUID workspaceId);

    List<Workspace> findAll();

    Page<Workspace> findAll(Pageable pageable);

    List<Workspace> findByOwnerUserId(UUID ownerUserId);

    Page<Workspace> findByOwnerUserId(UUID ownerUserId, Pageable pageable);

    Page<Workspace> findAccessibleByUser(UUID userId, Pageable pageable);

    void deleteById(UUID workspaceId);

    boolean existsById(UUID workspaceId);
}
