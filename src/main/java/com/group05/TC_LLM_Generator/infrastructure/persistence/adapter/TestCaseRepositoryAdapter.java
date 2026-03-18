package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.TestCaseRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter for TestCase repository
 */
@Component
@RequiredArgsConstructor
public class TestCaseRepositoryAdapter implements TestCaseRepositoryPort {

    private final TestCaseRepository jpaRepository;

    @Override
    public TestCase save(TestCase testCase) {
        return jpaRepository.save(testCase);
    }

    @Override
    public Optional<TestCase> findById(UUID testCaseId) {
        return jpaRepository.findById(testCaseId);
    }

    @Override
    public List<TestCase> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<TestCase> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public List<TestCase> findByUserStoryId(UUID userStoryId) {
        return jpaRepository.findByUserStory_UserStoryId(userStoryId);
    }

    @Override
    public Page<TestCase> findByUserStoryId(UUID userStoryId, Pageable pageable) {
        return jpaRepository.findByUserStory_UserStoryId(userStoryId, pageable);
    }

    @Override
    public List<TestCase> findByAcceptanceCriteriaId(UUID acceptanceCriteriaId) {
        return jpaRepository.findByAcceptanceCriteria_AcceptanceCriteriaId(acceptanceCriteriaId);
    }

    @Override
    public Page<TestCase> findByAcceptanceCriteriaId(UUID acceptanceCriteriaId, Pageable pageable) {
        return jpaRepository.findByAcceptanceCriteria_AcceptanceCriteriaId(acceptanceCriteriaId, pageable);
    }

    @Override
    public List<TestCase> findByTestCaseTypeId(UUID testCaseTypeId) {
        return jpaRepository.findByTestCaseType_TestCaseTypeId(testCaseTypeId);
    }

    @Override
    public Page<TestCase> findByTestCaseTypeId(UUID testCaseTypeId, Pageable pageable) {
        return jpaRepository.findByTestCaseType_TestCaseTypeId(testCaseTypeId, pageable);
    }

    @Override
    public List<TestCase> findByAcceptanceCriteriaIdAndGeneratedByAiTrue(UUID acceptanceCriteriaId) {
        return jpaRepository.findByAcceptanceCriteria_AcceptanceCriteriaIdAndGeneratedByAiTrue(acceptanceCriteriaId);
    }

    @Override
    public List<TestCase> findByAcceptanceCriteriaIdAndGeneratedByAiFalse(UUID acceptanceCriteriaId) {
        return jpaRepository.findByAcceptanceCriteria_AcceptanceCriteriaIdAndGeneratedByAiFalse(acceptanceCriteriaId);
    }

    @Override
    public List<TestCase> findByTitleContaining(String title) {
        return jpaRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public Page<TestCase> findByTitleContaining(String title, Pageable pageable) {
        return jpaRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    public void deleteById(UUID testCaseId) {
        jpaRepository.deleteById(testCaseId);
    }

    @Override
    public boolean existsById(UUID testCaseId) {
        return jpaRepository.existsById(testCaseId);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByProjectId(UUID projectId) {
        return jpaRepository.countByProjectId(projectId);
    }

    @Override
    public long countByProjectIdAndGeneratedByAi(UUID projectId, boolean generatedByAi) {
        return jpaRepository.countByProjectIdAndGeneratedByAi(projectId, generatedByAi);
    }
}
