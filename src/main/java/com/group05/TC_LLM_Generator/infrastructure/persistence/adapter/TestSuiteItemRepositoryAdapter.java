package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.TestSuiteItemRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuiteItem;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.TestSuiteItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestSuiteItemRepositoryAdapter implements TestSuiteItemRepositoryPort {

    private final TestSuiteItemRepository jpaRepository;

    @Override
    public TestSuiteItem save(TestSuiteItem item) {
        return jpaRepository.save(item);
    }

    @Override
    public List<TestSuiteItem> findByTestSuiteId(UUID testSuiteId) {
        return jpaRepository.findByTestSuite_TestSuiteId(testSuiteId);
    }

    @Override
    public List<TestSuiteItem> findByTestSuiteIdOrdered(UUID testSuiteId) {
        // Use JOIN FETCH to eagerly load testCase — prevents LazyInitializationException
        return jpaRepository.findByTestSuiteIdOrderedWithTestCase(testSuiteId);
    }

    @Override
    public Optional<TestSuiteItem> findByTestSuiteIdAndTestCaseId(UUID testSuiteId, UUID testCaseId) {
        return jpaRepository.findByTestSuite_TestSuiteIdAndTestCase_TestCaseId(testSuiteId, testCaseId);
    }

    @Override
    public void deleteByTestSuiteIdAndTestCaseId(UUID testSuiteId, UUID testCaseId) {
        jpaRepository.deleteByTestSuite_TestSuiteIdAndTestCase_TestCaseId(testSuiteId, testCaseId);
    }

    @Override
    public long countByTestSuiteId(UUID testSuiteId) {
        return jpaRepository.countByTestSuite_TestSuiteId(testSuiteId);
    }
}
