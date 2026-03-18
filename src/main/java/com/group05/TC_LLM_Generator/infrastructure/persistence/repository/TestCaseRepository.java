package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TestCase entity
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {

    @EntityGraph(attributePaths = {"userStory", "acceptanceCriteria", "testCaseType"})
    List<TestCase> findByUserStory_UserStoryId(UUID userStoryId);

    @EntityGraph(attributePaths = {"userStory", "acceptanceCriteria", "testCaseType"})
    Page<TestCase> findByUserStory_UserStoryId(UUID userStoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"userStory", "acceptanceCriteria", "testCaseType"})
    List<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaId(UUID acceptanceCriteriaId);

    @EntityGraph(attributePaths = {"userStory", "acceptanceCriteria", "testCaseType"})
    Page<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaId(UUID acceptanceCriteriaId, Pageable pageable);

    List<TestCase> findByTestCaseType_TestCaseTypeId(UUID testCaseTypeId);

    Page<TestCase> findByTestCaseType_TestCaseTypeId(UUID testCaseTypeId, Pageable pageable);

    List<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaIdAndGeneratedByAiTrue(UUID acceptanceCriteriaId);

    List<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaIdAndGeneratedByAiFalse(UUID acceptanceCriteriaId);

    List<TestCase> findByTitleContainingIgnoreCase(String title);

    Page<TestCase> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.userStory.project.projectId = :projectId")
    long countByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.userStory.project.projectId = :projectId AND tc.generatedByAi = :generatedByAi")
    long countByProjectIdAndGeneratedByAi(@Param("projectId") UUID projectId, @Param("generatedByAi") boolean generatedByAi);
}
