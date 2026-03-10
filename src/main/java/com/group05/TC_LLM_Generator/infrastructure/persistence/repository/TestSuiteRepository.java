package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TestSuite entity
 */
@Repository
public interface TestSuiteRepository extends JpaRepository<TestSuite, UUID> {

    /**
     * Find test suites by project ID
     * @param projectId project ID
     * @return List of test suites
     */
    List<TestSuite> findByProject_ProjectId(UUID projectId);

    /**
     * Find test suites by name containing (case-insensitive search)
     * @param projectId project ID
     * @param name search term
     * @return List of matching test suites
     */
    List<TestSuite> findByProject_ProjectIdAndNameContainingIgnoreCase(UUID projectId, String name);

    /**
     * Find test suites ordered by creation date
     * @param projectId project ID
     * @return List of test suites ordered by creation date
     */
    List<TestSuite> findByProject_ProjectIdOrderByCreatedAtDesc(UUID projectId);

    Page<TestSuite> findByProject_ProjectId(UUID projectId, Pageable pageable);
}
