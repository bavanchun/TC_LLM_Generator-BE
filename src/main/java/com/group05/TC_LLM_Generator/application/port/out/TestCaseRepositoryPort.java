package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for TestCase repository operations.
 * Defines the contract for persistence operations on TestCase entities.
 */
public interface TestCaseRepositoryPort {

    TestCase save(TestCase testCase);

    Optional<TestCase> findById(UUID testCaseId);

    List<TestCase> findAll();

    Page<TestCase> findAll(Pageable pageable);

    List<TestCase> findByUserStoryId(UUID userStoryId);

    Page<TestCase> findByUserStoryId(UUID userStoryId, Pageable pageable);

    List<TestCase> findByAcceptanceCriteriaId(UUID acceptanceCriteriaId);

    Page<TestCase> findByAcceptanceCriteriaId(UUID acceptanceCriteriaId, Pageable pageable);

    List<TestCase> findByTestCaseTypeId(UUID testCaseTypeId);

    Page<TestCase> findByTestCaseTypeId(UUID testCaseTypeId, Pageable pageable);

    List<TestCase> findByAcceptanceCriteriaIdAndGeneratedByAiTrue(UUID acceptanceCriteriaId);

    List<TestCase> findByAcceptanceCriteriaIdAndGeneratedByAiFalse(UUID acceptanceCriteriaId);

    List<TestCase> findByTitleContaining(String title);

    Page<TestCase> findByTitleContaining(String title, Pageable pageable);

    void deleteById(UUID testCaseId);

    boolean existsById(UUID testCaseId);

    long count();

    long countByProjectId(UUID projectId);

    long countByProjectIdAndGeneratedByAi(UUID projectId, boolean generatedByAi);
}
