package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuiteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TestSuiteItem entity.
 * Queries that return items for presentation layer use JOIN FETCH
 * to eagerly load the testCase association and avoid
 * LazyInitializationException in the controller/mapper layer.
 */
@Repository
public interface TestSuiteItemRepository extends JpaRepository<TestSuiteItem, UUID> {

    List<TestSuiteItem> findByTestSuite_TestSuiteId(UUID testSuiteId);

    List<TestSuiteItem> findByTestCase_TestCaseId(UUID testCaseId);

    /**
     * Find test suite items ordered by display order, eagerly fetching testCase.
     */
    @Query("SELECT tsi FROM TestSuiteItem tsi JOIN FETCH tsi.testCase WHERE tsi.testSuite.testSuiteId = :testSuiteId ORDER BY tsi.displayOrder ASC")
    List<TestSuiteItem> findByTestSuiteIdOrderedWithTestCase(@Param("testSuiteId") UUID testSuiteId);

    // Original method kept for backward compatibility
    List<TestSuiteItem> findByTestSuite_TestSuiteIdOrderByDisplayOrderAsc(UUID testSuiteId);

    Optional<TestSuiteItem> findByTestSuite_TestSuiteIdAndTestCase_TestCaseId(UUID testSuiteId, UUID testCaseId);

    void deleteByTestSuite_TestSuiteIdAndTestCase_TestCaseId(UUID testSuiteId, UUID testCaseId);

    long countByTestSuite_TestSuiteId(UUID testSuiteId);
}
