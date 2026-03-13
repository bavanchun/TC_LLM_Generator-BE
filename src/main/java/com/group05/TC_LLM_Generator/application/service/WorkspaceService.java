package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.WorkspaceRepositoryPort;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.Action;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.EntityType;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Create a new workspace
     */
    @Transactional
    public Workspace createWorkspace(Workspace workspace) {
        Workspace saved = workspaceRepository.save(workspace);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.WORKSPACE, Action.CREATED,
                saved.getWorkspaceId().toString(),
                null,  // top-level entity, no parent
                null,  // payload resolved by controller/assembler if needed
                saved.getOwnerUser().getUserId().toString()
        ));

        return saved;
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

    public boolean hasAnyWorkspace(UUID userId) {
        return !workspaceRepository.findByOwnerUserId(userId).isEmpty();
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

        Workspace saved = workspaceRepository.save(existingWorkspace);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.WORKSPACE, Action.UPDATED,
                saved.getWorkspaceId().toString(),
                null,
                null,
                saved.getOwnerUser().getUserId().toString()
        ));

        return saved;
    }

    /**
     * Delete workspace by ID
     */
    @Transactional
    public void deleteWorkspace(UUID workspaceId, String performedByUserId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new IllegalArgumentException("Workspace not found: " + workspaceId);
        }
        workspaceRepository.deleteById(workspaceId);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.WORKSPACE, Action.DELETED,
                workspaceId.toString(),
                null,
                null,
                performedByUserId
        ));
    }

    /**
     * Check if workspace exists
     */
    public boolean workspaceExists(UUID workspaceId) {
        return workspaceRepository.existsById(workspaceId);
    }

    /**
     * Check if user is a member or owner of the workspace
     * @param workspaceId workspace ID
     * @param userId user ID
     * @return true if user is owner or member
     */
    public boolean isMemberOrOwner(UUID workspaceId, UUID userId) {
        Optional<Workspace> ws = workspaceRepository.findById(workspaceId);
        if (ws.isPresent() && ws.get().getOwnerUser().getUserId().equals(userId)) {
            return true;
        }
        return workspaceMemberRepository
                .findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId)
                .isPresent();
    }
}
