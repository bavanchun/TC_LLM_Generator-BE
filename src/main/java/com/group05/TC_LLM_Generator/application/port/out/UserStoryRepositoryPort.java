package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for UserStory repository operations.
 * Defines the contract for persistence operations on UserStory entities.
 */
public interface UserStoryRepositoryPort {

    UserStory save(UserStory userStory);

    Optional<UserStory> findById(UUID userStoryId);

    List<UserStory> findAll();

    Page<UserStory> findAll(Pageable pageable);

    List<UserStory> findByProjectId(UUID projectId);

    Page<UserStory> findByProjectId(UUID projectId, Pageable pageable);

    void deleteById(UUID userStoryId);

    boolean existsById(UUID userStoryId);
}
