package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TestSuite entity.
 * All queries eagerly fetch the project association to avoid
 * LazyInitializationException in the presentation layer mapper.
 */
@Repository
public interface TestSuiteRepository extends JpaRepository<TestSuite, UUID> {

    @Query("SELECT ts FROM TestSuite ts JOIN FETCH ts.project WHERE ts.testSuiteId = :id")
    Optional<TestSuite> findByIdWithProject(@Param("id") UUID id);

    @Query("SELECT ts FROM TestSuite ts JOIN FETCH ts.project WHERE ts.project.projectId = :projectId")
    List<TestSuite> findByProject_ProjectIdWithProject(@Param("projectId") UUID projectId);

    @Query(value = "SELECT ts FROM TestSuite ts JOIN FETCH ts.project WHERE ts.project.projectId = :projectId",
           countQuery = "SELECT COUNT(ts) FROM TestSuite ts WHERE ts.project.projectId = :projectId")
    Page<TestSuite> findByProject_ProjectIdWithProject(@Param("projectId") UUID projectId, Pageable pageable);

    @Query(value = "SELECT ts FROM TestSuite ts JOIN FETCH ts.project",
           countQuery = "SELECT COUNT(ts) FROM TestSuite ts")
    Page<TestSuite> findAllWithProject(Pageable pageable);

    // Original methods kept for backward compatibility
    List<TestSuite> findByProject_ProjectId(UUID projectId);
    List<TestSuite> findByProject_ProjectIdAndNameContainingIgnoreCase(UUID projectId, String name);
    List<TestSuite> findByProject_ProjectIdOrderByCreatedAtDesc(UUID projectId);
    Page<TestSuite> findByProject_ProjectId(UUID projectId, Pageable pageable);
}
