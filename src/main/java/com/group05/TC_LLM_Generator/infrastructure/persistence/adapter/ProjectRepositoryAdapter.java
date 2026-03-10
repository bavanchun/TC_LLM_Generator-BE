package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.ProjectRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter for Project repository
 */
@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {

    private final ProjectRepository jpaRepository;

    @Override
    public Project save(Project project) {
        return jpaRepository.save(project);
    }

    @Override
    public Optional<Project> findById(UUID projectId) {
        return jpaRepository.findById(projectId);
    }

    @Override
    public Optional<Project> findByProjectKey(String projectKey) {
        return jpaRepository.findByProjectKey(projectKey);
    }

    @Override
    public List<Project> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Project> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public List<Project> findByWorkspaceId(UUID workspaceId) {
        return jpaRepository.findByWorkspace_WorkspaceId(workspaceId);
    }

    @Override
    public Page<Project> findByWorkspaceId(UUID workspaceId, Pageable pageable) {
        return jpaRepository.findByWorkspace_WorkspaceId(workspaceId, pageable);
    }

    @Override
    public List<Project> findByCreatedByUserId(UUID userId) {
        return jpaRepository.findByCreatedByUser_UserId(userId);
    }

    @Override
    public Page<Project> findByCreatedByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByCreatedByUser_UserId(userId, pageable);
    }

    @Override
    public Page<Project> findAccessibleByUser(UUID userId, Pageable pageable) {
        return jpaRepository.findAccessibleByUser(userId, pageable);
    }

    @Override
    public void deleteById(UUID projectId) {
        jpaRepository.deleteById(projectId);
    }

    @Override
    public boolean existsById(UUID projectId) {
        return jpaRepository.existsById(projectId);
    }

    @Override
    public boolean existsByProjectKey(String projectKey) {
        return jpaRepository.existsByProjectKey(projectKey);
    }
}
