package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.domain.model.enums.StoryStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for UserStory entity
 */
@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, UUID> {

    /**
     * Find user stories by project ID
     * @param projectId project ID
     * @return List of user stories
     */
    List<UserStory> findByProject_ProjectId(UUID projectId);

    /**
     * Find user stories by project ID with pagination
     * @param projectId project ID
     * @param pageable pagination parameters
     * @return Page of user stories
     */
    Page<UserStory> findByProject_ProjectId(UUID projectId, Pageable pageable);

    /**
     * Find user story by Jira issue key
     * @param jiraIssueKey Jira issue key
     * @return Optional of UserStory
     */
    Optional<UserStory> findByJiraIssueKey(String jiraIssueKey);

    /**
     * Find user story by Jira issue ID
     * @param jiraIssueId Jira issue ID
     * @return Optional of UserStory
     */
    Optional<UserStory> findByJiraIssueId(String jiraIssueId);

    /**
     * Find user stories by status
     * @param projectId project ID
     * @param status user story status
     * @return List of user stories with the specified status
     */
    List<UserStory> findByProject_ProjectIdAndStatus(UUID projectId, String status);

    /**
     * Find user stories by status
     * @param status user story status
     * @return List of user stories with the specified status
     */
    List<UserStory> findByStatus(String status);

    long countByProject_ProjectId(UUID projectId);

    long countByProject_ProjectIdAndStatus(UUID projectId, StoryStatus status);
}
