package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.TestCaseRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.TestSuiteItemRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.TestSuiteRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuiteItem;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestSuiteItemService {

    private final TestSuiteItemRepositoryPort testSuiteItemRepository;
    private final TestCaseRepositoryPort testCaseRepository;
    private final TestSuiteRepositoryPort testSuiteRepository;

    /**
     * Add a test case to a suite by their IDs.
     * Re-fetches entities within this transaction to ensure lazy proxies are usable.
     */
    @Transactional
    public TestSuiteItem addTestCaseToSuite(UUID testSuiteId, UUID testCaseId) {
        // Check if already exists
        if (testSuiteItemRepository.findByTestSuiteIdAndTestCaseId(testSuiteId, testCaseId).isPresent()) {
            throw new IllegalArgumentException("Test case already exists in this suite");
        }

        // Re-fetch within current session to avoid detached entity issues
        TestSuite testSuite = testSuiteRepository.findById(testSuiteId)
                .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));
        TestCase testCase = testCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));

        int nextOrder = (int) testSuiteItemRepository.countByTestSuiteId(testSuiteId) + 1;

        TestSuiteItem item = TestSuiteItem.builder()
                .testSuite(testSuite)
                .testCase(testCase)
                .displayOrder(nextOrder)
                .build();

        TestSuiteItem saved = testSuiteItemRepository.save(item);

        // Eagerly initialize lazy associations so the HATEOAS assembler
        // can access them after the transaction boundary closes
        TestCase tc = saved.getTestCase();
        Hibernate.initialize(tc.getUserStory());
        Hibernate.initialize(tc.getAcceptanceCriteria());
        Hibernate.initialize(tc.getTestCaseType());

        return saved;
    }

    /**
     * Legacy overload for backward compatibility - delegates to UUID-based method.
     */
    @Transactional
    public TestSuiteItem addTestCaseToSuite(TestSuite testSuite, TestCase testCase) {
        return addTestCaseToSuite(testSuite.getTestSuiteId(), testCase.getTestCaseId());
    }

    @Transactional
    public void removeTestCaseFromSuite(UUID testSuiteId, UUID testCaseId) {
        testSuiteItemRepository.findByTestSuiteIdAndTestCaseId(testSuiteId, testCaseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Test case " + testCaseId + " not found in suite " + testSuiteId));
        testSuiteItemRepository.deleteByTestSuiteIdAndTestCaseId(testSuiteId, testCaseId);
    }

    public List<TestSuiteItem> getTestCasesInSuite(UUID testSuiteId) {
        return testSuiteItemRepository.findByTestSuiteIdOrdered(testSuiteId);
    }
}
