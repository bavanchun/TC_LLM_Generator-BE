package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for AcceptanceCriteria entity
 */
@Repository
public interface AcceptanceCriteriaRepository extends JpaRepository<AcceptanceCriteria, UUID> {

    /**
     * Find acceptance criteria by user story ID
     * @param userStoryId user story ID
     * @return List of acceptance criteria
     */
    List<AcceptanceCriteria> findByUserStory_UserStoryId(UUID userStoryId);

    /**
     * Find acceptance criteria by user story ID ordered by order number
     * @param userStoryId user story ID
     * @return List of acceptance criteria ordered by orderNo
     */
    List<AcceptanceCriteria> findByUserStory_UserStoryIdOrderByOrderNoAsc(UUID userStoryId);

    /**
     * Delete all acceptance criteria for a user story
     * @param userStoryId user story ID
     */
    void deleteByUserStory_UserStoryId(UUID userStoryId);
}
