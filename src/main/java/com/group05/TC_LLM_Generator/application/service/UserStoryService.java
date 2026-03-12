package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.UserStoryRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for UserStory entity
 * Handles CRUD operations and user story-related use cases
 */
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.Action;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.EntityType;
import org.springframework.context.ApplicationEventPublisher;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoryService {

    private final UserStoryRepositoryPort userStoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Create a new user story with optional acceptance criteria (single transaction)
     */
    @Transactional
    public UserStory createUserStory(UserStory userStory, List<AcceptanceCriteria> acceptanceCriteriaList, String performedByUserId) {
        // Link AC entities to the story via cascade
        if (acceptanceCriteriaList != null && !acceptanceCriteriaList.isEmpty()) {
            for (AcceptanceCriteria ac : acceptanceCriteriaList) {
                ac.setUserStory(userStory);
                userStory.getAcceptanceCriteria().add(ac);
            }
        }

        UserStory saved = userStoryRepository.save(userStory);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.STORY, Action.CREATED,
                saved.getUserStoryId().toString(),
                saved.getProject().getProjectId().toString(),
                null,
                performedByUserId
        ));

        return saved;
    }

    /**
     * Create a new user story (without AC list — backward compatibility)
     */
    @Transactional
    public UserStory createUserStory(UserStory userStory, String performedByUserId) {
        return createUserStory(userStory, null, performedByUserId);
    }

    /**
     * Get user story by ID
     */
    public Optional<UserStory> getUserStoryById(UUID userStoryId) {
        return userStoryRepository.findById(userStoryId);
    }

    /**
     * Get all user stories
     */
    public List<UserStory> getAllUserStories() {
        return userStoryRepository.findAll();
    }

    /**
     * Get all user stories with pagination
     */
    public Page<UserStory> getAllUserStories(Pageable pageable) {
        return userStoryRepository.findAll(pageable);
    }

    /**
     * Get user stories by project ID
     */
    public List<UserStory> getUserStoriesByProject(UUID projectId) {
        return userStoryRepository.findByProjectId(projectId);
    }

    /**
     * Get user stories by project ID with pagination
     */
    public Page<UserStory> getUserStoriesByProject(UUID projectId, Pageable pageable) {
        return userStoryRepository.findByProjectId(projectId, pageable);
    }

    /**
     * Update user story (with optional AC replacement)
     */
    @Transactional
    public UserStory updateUserStory(UUID userStoryId, UserStory updatedUserStory,
                                      List<AcceptanceCriteria> newAcceptanceCriteria,
                                      String performedByUserId) {
        UserStory existing = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));

        if (updatedUserStory.getTitle() != null) {
            existing.setTitle(updatedUserStory.getTitle());
        }
        if (updatedUserStory.getDescription() != null) {
            existing.setDescription(updatedUserStory.getDescription());
        }
        if (updatedUserStory.getAsA() != null) {
            existing.setAsA(updatedUserStory.getAsA());
        }
        if (updatedUserStory.getIWantTo() != null) {
            existing.setIWantTo(updatedUserStory.getIWantTo());
        }
        if (updatedUserStory.getSoThat() != null) {
            existing.setSoThat(updatedUserStory.getSoThat());
        }
        if (updatedUserStory.getStatus() != null) {
            existing.setStatus(updatedUserStory.getStatus());
        }

        // Replace AC list if provided (orphanRemoval handles deletes)
        if (newAcceptanceCriteria != null) {
            existing.getAcceptanceCriteria().clear();
            for (AcceptanceCriteria ac : newAcceptanceCriteria) {
                ac.setUserStory(existing);
                existing.getAcceptanceCriteria().add(ac);
            }
        }

        UserStory saved = userStoryRepository.save(existing);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.STORY, Action.UPDATED,
                saved.getUserStoryId().toString(),
                saved.getProject().getProjectId().toString(),
                null,
                performedByUserId
        ));

        return saved;
    }

    /**
     * Update user story (without AC replacement — backward compatibility)
     */
    @Transactional
    public UserStory updateUserStory(UUID userStoryId, UserStory updatedUserStory, String performedByUserId) {
        return updateUserStory(userStoryId, updatedUserStory, null, performedByUserId);
    }

    /**
     * Delete user story by ID
     */
    @Transactional
    public void deleteUserStory(UUID userStoryId, String performedByUserId) {
        UserStory existingUserStory = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));

        String projectId = existingUserStory.getProject().getProjectId().toString();

        userStoryRepository.deleteById(userStoryId);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.STORY, Action.DELETED,
                userStoryId.toString(),
                projectId,
                null,
                performedByUserId
        ));
    }

    /**
     * Check if user story exists
     */
    public boolean userStoryExists(UUID userStoryId) {
        return userStoryRepository.existsById(userStoryId);
    }
}
