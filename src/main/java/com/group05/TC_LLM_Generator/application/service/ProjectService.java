package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.ProjectRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.ProjectMemberRepositoryPort;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.Action;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.EntityType;
import com.group05.TC_LLM_Generator.domain.model.enums.ProjectRole;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for Project entity
 * Handles CRUD operations and project-related use cases
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepositoryPort projectRepository;
    private final ProjectMemberRepositoryPort projectMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Create a new project
     */
    @Transactional
    public Project createProject(Project project) {
        UUID workspaceId = project.getWorkspace().getWorkspaceId();
        if (projectRepository.existsByWorkspaceIdAndProjectKey(workspaceId, project.getProjectKey())) {
            throw new IllegalArgumentException("Project key already exists in this workspace: " + project.getProjectKey());
        }
        
        Project saved = projectRepository.save(project);

        // Auto-create ProjectMember with Lead role for the creator
        ProjectMember ownerMember = ProjectMember.builder()
                .project(saved)
                .user(saved.getCreatedByUser())
                .role(ProjectRole.Lead.name())
                .joinedAt(Instant.now())
                .build();
        projectMemberRepository.save(ownerMember);
        
        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.PROJECT, Action.CREATED,
                saved.getProjectId().toString(),
                saved.getWorkspace().getWorkspaceId().toString(), // parentId is workspaceId
                null, // payload
                saved.getCreatedByUser().getUserId().toString() // performedBy
        ));
        
        return saved;
    }

    /**
     * Get project by ID
     */
    public Optional<Project> getProjectById(UUID projectId) {
        return projectRepository.findById(projectId);
    }

    /**
     * Get project by project key
     */
    public Optional<Project> getProjectByKey(String projectKey) {
        return projectRepository.findByProjectKey(projectKey);
    }

    /**
     * Get all projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Get all projects with pagination
     */
    public Page<Project> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    /**
     * Get projects by workspace ID
     */
    public List<Project> getProjectsByWorkspace(UUID workspaceId) {
        return projectRepository.findByWorkspaceId(workspaceId);
    }

    /**
     * Get projects by workspace ID with pagination
     */
    public Page<Project> getProjectsByWorkspace(UUID workspaceId, Pageable pageable) {
        return projectRepository.findByWorkspaceId(workspaceId, pageable);
    }

    /**
     * Get projects by creator user ID
     */
    public List<Project> getProjectsByCreator(UUID userId) {
        return projectRepository.findByCreatedByUserId(userId);
    }

    public Page<Project> getProjectsByCreator(UUID userId, Pageable pageable) {
        return projectRepository.findByCreatedByUserId(userId, pageable);
    }

    public Page<Project> getAccessibleProjects(UUID userId, Pageable pageable) {
        return projectRepository.findAccessibleByUser(userId, pageable);
    }

    /**
     * Update project
     */
    @Transactional
    public Project updateProject(UUID projectId, Project updatedProject, String performedByUserId) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        // Update fields
        if (updatedProject.getName() != null) {
            existingProject.setName(updatedProject.getName());
        }
        
        if (updatedProject.getDescription() != null) {
            existingProject.setDescription(updatedProject.getDescription());
        }
        
        if (updatedProject.getStatus() != null) {
            existingProject.setStatus(updatedProject.getStatus());
        }
        
        if (updatedProject.getJiraSiteId() != null) {
            existingProject.setJiraSiteId(updatedProject.getJiraSiteId());
        }
        
        if (updatedProject.getJiraProjectKey() != null) {
            existingProject.setJiraProjectKey(updatedProject.getJiraProjectKey());
        }

        Project saved = projectRepository.save(existingProject);
        
        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.PROJECT, Action.UPDATED,
                saved.getProjectId().toString(),
                saved.getWorkspace().getWorkspaceId().toString(),
                null,
                performedByUserId
        ));
        
        return saved;
    }

    /**
     * Delete project by ID
     */
    @Transactional
    public void deleteProject(UUID projectId, String performedByUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
                
        String workspaceId = project.getWorkspace().getWorkspaceId().toString();
        
        projectRepository.deleteById(projectId);
        
        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.PROJECT, Action.DELETED,
                projectId.toString(),
                workspaceId,
                null,
                performedByUserId
        ));
    }

    /**
     * Check if project exists
     */
    public boolean projectExists(UUID projectId) {
        return projectRepository.existsById(projectId);
    }

    /**
     * Check if project key exists within a workspace
     */
    public boolean projectKeyExists(UUID workspaceId, String projectKey) {
        return projectRepository.existsByWorkspaceIdAndProjectKey(workspaceId, projectKey);
    }
}
