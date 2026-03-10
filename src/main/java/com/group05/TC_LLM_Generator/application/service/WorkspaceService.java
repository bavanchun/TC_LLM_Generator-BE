package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.WorkspaceRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for Workspace entity
 * Handles CRUD operations and workspace-related use cases
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceService {

    private final WorkspaceRepositoryPort workspaceRepository;

    /**
     * Create a new workspace
     */
    @Transactional
    public Workspace createWorkspace(Workspace workspace) {
        return workspaceRepository.save(workspace);
    }

    /**
     * Get workspace by ID
     */
    public Optional<Workspace> getWorkspaceById(UUID workspaceId) {
        return workspaceRepository.findById(workspaceId);
    }

    /**
     * Get all workspaces
     */
    public List<Workspace> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    /**
     * Get all workspaces with pagination
     */
    public Page<Workspace> getAllWorkspaces(Pageable pageable) {
        return workspaceRepository.findAll(pageable);
    }

    /**
     * Get workspaces by owner user ID
     */
    public List<Workspace> getWorkspacesByOwner(UUID ownerUserId) {
        return workspaceRepository.findByOwnerUserId(ownerUserId);
    }

    public Page<Workspace> getWorkspacesByOwner(UUID ownerUserId, Pageable pageable) {
        return workspaceRepository.findByOwnerUserId(ownerUserId, pageable);
    }

    public Page<Workspace> getAccessibleWorkspaces(UUID userId, Pageable pageable) {
        return workspaceRepository.findAccessibleByUser(userId, pageable);
    }

    /**
     * Update workspace
     */
    @Transactional
    public Workspace updateWorkspace(UUID workspaceId, Workspace updatedWorkspace) {
        Workspace existingWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + workspaceId));

        // Update fields
        if (updatedWorkspace.getName() != null) {
            existingWorkspace.setName(updatedWorkspace.getName());
        }
        
        if (updatedWorkspace.getDescription() != null) {
            existingWorkspace.setDescription(updatedWorkspace.getDescription());
        }

        return workspaceRepository.save(existingWorkspace);
    }

    /**
     * Delete workspace by ID
     */
    @Transactional
    public void deleteWorkspace(UUID workspaceId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new IllegalArgumentException("Workspace not found: " + workspaceId);
        }
        workspaceRepository.deleteById(workspaceId);
    }

    /**
     * Check if workspace exists
     */
    public boolean workspaceExists(UUID workspaceId) {
        return workspaceRepository.existsById(workspaceId);
    }
}
