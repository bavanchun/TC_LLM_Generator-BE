package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.UserStoryRepositoryPort;
import com.group05.TC_LLM_Generator.domain.model.enums.StoryStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.UserStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter for UserStory repository
 */
@Component
@RequiredArgsConstructor
public class UserStoryRepositoryAdapter implements UserStoryRepositoryPort {

    private final UserStoryRepository jpaRepository;

    @Override
    public UserStory save(UserStory userStory) {
        return jpaRepository.save(userStory);
    }

    @Override
    public Optional<UserStory> findById(UUID userStoryId) {
        return jpaRepository.findById(userStoryId);
    }

    @Override
    public List<UserStory> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<UserStory> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public List<UserStory> findByProjectId(UUID projectId) {
        return jpaRepository.findByProject_ProjectId(projectId);
    }

    @Override
    public Page<UserStory> findByProjectId(UUID projectId, Pageable pageable) {
        return jpaRepository.findByProject_ProjectId(projectId, pageable);
    }

    @Override
    public void deleteById(UUID userStoryId) {
        jpaRepository.deleteById(userStoryId);
    }

    @Override
    public boolean existsById(UUID userStoryId) {
        return jpaRepository.existsById(userStoryId);
    }

    @Override
    public long countByProjectId(UUID projectId) {
        return jpaRepository.countByProject_ProjectId(projectId);
    }

    @Override
    public long countByProjectIdAndStatus(UUID projectId, String status) {
        return jpaRepository.countByProject_ProjectIdAndStatus(projectId, StoryStatus.valueOf(status));
    }
}
