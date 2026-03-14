package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.TestCaseRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for TestCase entity
 * Handles CRUD operations and test case-related use cases
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestCaseService {

    private final TestCaseRepositoryPort testCaseRepository;

    /**
     * Create a new test case
     */
    @Transactional
    public TestCase createTestCase(TestCase testCase) {
        return testCaseRepository.save(testCase);
    }

    /**
     * Get test case by ID
     */
    public Optional<TestCase> getTestCaseById(UUID testCaseId) {
        return testCaseRepository.findById(testCaseId);
    }

    /**
     * Get all test cases
     */
    public List<TestCase> getAllTestCases() {
        return testCaseRepository.findAll();
    }

    /**
     * Get all test cases with pagination
     */
    public Page<TestCase> getAllTestCases(Pageable pageable) {
        return testCaseRepository.findAll(pageable);
    }

    /**
     * Get test cases by user story ID
     */
    public List<TestCase> getTestCasesByUserStory(UUID userStoryId) {
        return testCaseRepository.findByUserStoryId(userStoryId);
    }

    /**
     * Get test cases by user story ID with pagination
     */
    public Page<TestCase> getTestCasesByUserStory(UUID userStoryId, Pageable pageable) {
        return testCaseRepository.findByUserStoryId(userStoryId, pageable);
    }

    /**
     * Get test cases by acceptance criteria ID
     */
    public List<TestCase> getTestCasesByAcceptanceCriteria(UUID acceptanceCriteriaId) {
        return testCaseRepository.findByAcceptanceCriteriaId(acceptanceCriteriaId);
    }

    /**
     * Get test cases by acceptance criteria ID with pagination
     */
    public Page<TestCase> getTestCasesByAcceptanceCriteria(UUID acceptanceCriteriaId, Pageable pageable) {
        return testCaseRepository.findByAcceptanceCriteriaId(acceptanceCriteriaId, pageable);
    }

    /**
     * Get test cases by test case type ID
     */
    public List<TestCase> getTestCasesByType(UUID testCaseTypeId) {
        return testCaseRepository.findByTestCaseTypeId(testCaseTypeId);
    }

    /**
     * Get AI-generated test cases by acceptance criteria
     */
    public List<TestCase> getAIGeneratedTestCases(UUID acceptanceCriteriaId) {
        return testCaseRepository.findByAcceptanceCriteriaIdAndGeneratedByAiTrue(acceptanceCriteriaId);
    }

    /**
     * Get manually created test cases by acceptance criteria
     */
    public List<TestCase> getManualTestCases(UUID acceptanceCriteriaId) {
        return testCaseRepository.findByAcceptanceCriteriaIdAndGeneratedByAiFalse(acceptanceCriteriaId);
    }

    /**
     * Search test cases by title
     */
    public List<TestCase> searchTestCasesByTitle(String title) {
        return testCaseRepository.findByTitleContaining(title);
    }

    /**
     * Search test cases by title with pagination
     */
    public Page<TestCase> searchTestCasesByTitle(String title, Pageable pageable) {
        return testCaseRepository.findByTitleContaining(title, pageable);
    }

    /**
     * Update test case
     */
    @Transactional
    public TestCase updateTestCase(UUID testCaseId, TestCase updatedTestCase) {
        TestCase existingTestCase = testCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));

        // Update fields
        if (updatedTestCase.getTitle() != null) {
            existingTestCase.setTitle(updatedTestCase.getTitle());
        }
        
        if (updatedTestCase.getPreconditions() != null) {
            existingTestCase.setPreconditions(updatedTestCase.getPreconditions());
        }
        
        if (updatedTestCase.getSteps() != null) {
            existingTestCase.setSteps(updatedTestCase.getSteps());
        }
        
        if (updatedTestCase.getExpectedResult() != null) {
            existingTestCase.setExpectedResult(updatedTestCase.getExpectedResult());
        }
        
        if (updatedTestCase.getCustomFieldsJson() != null) {
            existingTestCase.setCustomFieldsJson(updatedTestCase.getCustomFieldsJson());
        }

        TestCase saved = testCaseRepository.save(existingTestCase);

        // Eagerly initialize lazy relations so the HATEOAS assembler
        // can access them after the transaction boundary closes
        Hibernate.initialize(saved.getUserStory());
        Hibernate.initialize(saved.getAcceptanceCriteria());
        Hibernate.initialize(saved.getTestCaseType());

        return saved;
    }

    /**
     * Delete test case by ID
     */
    @Transactional
    public void deleteTestCase(UUID testCaseId) {
        if (!testCaseRepository.existsById(testCaseId)) {
            throw new IllegalArgumentException("Test case not found: " + testCaseId);
        }
        testCaseRepository.deleteById(testCaseId);
    }

    /**
     * Check if test case exists
     */
    public boolean testCaseExists(UUID testCaseId) {
        return testCaseRepository.existsById(testCaseId);
    }
}
