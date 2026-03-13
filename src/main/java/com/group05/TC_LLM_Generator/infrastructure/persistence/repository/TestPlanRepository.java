package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.domain.model.enums.TestPlanStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TestPlan entity
 */
@Repository
public interface TestPlanRepository extends JpaRepository<TestPlan, UUID> {

    List<TestPlan> findByProject_ProjectId(UUID projectId);

    Page<TestPlan> findByProject_ProjectId(UUID projectId, Pageable pageable);

    Page<TestPlan> findByProject_ProjectIdAndStatus(UUID projectId, TestPlanStatus status, Pageable pageable);

    List<TestPlan> findByProject_ProjectIdOrderByCreatedAtDesc(UUID projectId);

    List<TestPlan> findByCreatedByUser_UserId(UUID userId);

    List<TestPlan> findByProject_ProjectIdAndNameContainingIgnoreCase(UUID projectId, String name);

    @Query("SELECT us FROM TestPlan tp JOIN tp.userStories us WHERE tp.testPlanId = :testPlanId")
    Page<UserStory> findStoriesByTestPlanId(@Param("testPlanId") UUID testPlanId, Pageable pageable);
}
