package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.UserStoryRepositoryPort;
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
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoryService {

    private final UserStoryRepositoryPort userStoryRepository;

    /**
     * Create a new user story
     */
    @Transactional
    public UserStory createUserStory(UserStory userStory) {
        return userStoryRepository.save(userStory);
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
     * Update user story
     */
    @Transactional
    public UserStory updateUserStory(UUID userStoryId, UserStory updatedUserStory) {
        UserStory existingUserStory = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));

        if (updatedUserStory.getTitle() != null) {
            existingUserStory.setTitle(updatedUserStory.getTitle());
        }

        if (updatedUserStory.getDescription() != null) {
            existingUserStory.setDescription(updatedUserStory.getDescription());
        }

        if (updatedUserStory.getStatus() != null) {
            existingUserStory.setStatus(updatedUserStory.getStatus());
        }

        return userStoryRepository.save(existingUserStory);
    }

    /**
     * Delete user story by ID
     */
    @Transactional
    public void deleteUserStory(UUID userStoryId) {
        if (!userStoryRepository.existsById(userStoryId)) {
            throw new IllegalArgumentException("User story not found: " + userStoryId);
        }
        userStoryRepository.deleteById(userStoryId);
    }

    /**
     * Check if user story exists
     */
    public boolean userStoryExists(UUID userStoryId) {
        return userStoryRepository.existsById(userStoryId);
    }
}
