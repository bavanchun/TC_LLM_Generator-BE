package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for TestSuite repository operations.
 * Defines the contract for persistence operations on TestSuite entities.
 */
public interface TestSuiteRepositoryPort {

    TestSuite save(TestSuite testSuite);

    Optional<TestSuite> findById(UUID testSuiteId);

    List<TestSuite> findAll();

    Page<TestSuite> findAll(Pageable pageable);

    List<TestSuite> findByProjectId(UUID projectId);

    Page<TestSuite> findByProjectId(UUID projectId, Pageable pageable);

    void deleteById(UUID testSuiteId);

    boolean existsById(UUID testSuiteId);

    long countByProjectId(UUID projectId);
}
