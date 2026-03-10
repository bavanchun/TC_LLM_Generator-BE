package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.ProjectRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Create a new project
     */
    @Transactional
    public Project createProject(Project project) {
        if (projectRepository.existsByProjectKey(project.getProjectKey())) {
            throw new IllegalArgumentException("Project key already exists: " + project.getProjectKey());
        }
        return projectRepository.save(project);
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
    public Project updateProject(UUID projectId, Project updatedProject) {
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

        return projectRepository.save(existingProject);
    }

    /**
     * Delete project by ID
     */
    @Transactional
    public void deleteProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }
        projectRepository.deleteById(projectId);
    }

    /**
     * Check if project exists
     */
    public boolean projectExists(UUID projectId) {
        return projectRepository.existsById(projectId);
    }

    /**
     * Check if project key exists
     */
    public boolean projectKeyExists(String projectKey) {
        return projectRepository.existsByProjectKey(projectKey);
    }
}
