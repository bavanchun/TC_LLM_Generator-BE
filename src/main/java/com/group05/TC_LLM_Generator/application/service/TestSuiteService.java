package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.TestSuiteRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestSuiteService {

    private final TestSuiteRepositoryPort testSuiteRepository;

    @Transactional
    public TestSuite createTestSuite(TestSuite testSuite) {
        return testSuiteRepository.save(testSuite);
    }

    public Optional<TestSuite> getTestSuiteById(UUID testSuiteId) {
        return testSuiteRepository.findById(testSuiteId);
    }

    public List<TestSuite> getAllTestSuites() {
        return testSuiteRepository.findAll();
    }

    public Page<TestSuite> getAllTestSuites(Pageable pageable) {
        return testSuiteRepository.findAll(pageable);
    }

    public List<TestSuite> getTestSuitesByProject(UUID projectId) {
        return testSuiteRepository.findByProjectId(projectId);
    }

    public Page<TestSuite> getTestSuitesByProject(UUID projectId, Pageable pageable) {
        return testSuiteRepository.findByProjectId(projectId, pageable);
    }

    @Transactional
    public TestSuite updateTestSuite(UUID testSuiteId, TestSuite updatedTestSuite) {
        TestSuite existingTestSuite = testSuiteRepository.findById(testSuiteId)
                .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));

        if (updatedTestSuite.getName() != null) {
            existingTestSuite.setName(updatedTestSuite.getName());
        }

        if (updatedTestSuite.getDescription() != null) {
            existingTestSuite.setDescription(updatedTestSuite.getDescription());
        }

        return testSuiteRepository.save(existingTestSuite);
    }

    @Transactional
    public void deleteTestSuite(UUID testSuiteId) {
        if (!testSuiteRepository.existsById(testSuiteId)) {
            throw new IllegalArgumentException("Test suite not found: " + testSuiteId);
        }
        testSuiteRepository.deleteById(testSuiteId);
    }

    public boolean testSuiteExists(UUID testSuiteId) {
        return testSuiteRepository.existsById(testSuiteId);
    }
}
