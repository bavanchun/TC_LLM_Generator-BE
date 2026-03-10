package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.WorkspaceRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter for Workspace repository
 */
@Component
@RequiredArgsConstructor
public class WorkspaceRepositoryAdapter implements WorkspaceRepositoryPort {

    private final WorkspaceRepository jpaRepository;

    @Override
    public Workspace save(Workspace workspace) {
        return jpaRepository.save(workspace);
    }

    @Override
    public Optional<Workspace> findById(UUID workspaceId) {
        return jpaRepository.findById(workspaceId);
    }

    @Override
    public List<Workspace> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Workspace> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public List<Workspace> findByOwnerUserId(UUID ownerUserId) {
        return jpaRepository.findByOwnerUser_UserId(ownerUserId);
    }

    @Override
    public Page<Workspace> findByOwnerUserId(UUID ownerUserId, Pageable pageable) {
        return jpaRepository.findByOwnerUser_UserId(ownerUserId, pageable);
    }

    @Override
    public Page<Workspace> findAccessibleByUser(UUID userId, Pageable pageable) {
        return jpaRepository.findAccessibleByUser(userId, pageable);
    }

    @Override
    public void deleteById(UUID workspaceId) {
        jpaRepository.deleteById(workspaceId);
    }

    @Override
    public boolean existsById(UUID workspaceId) {
        return jpaRepository.existsById(workspaceId);
    }
}
