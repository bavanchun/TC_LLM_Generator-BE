package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.domain.model.enums.TestPlanStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for TestPlan repository operations.
 * Defines the contract for persistence operations on TestPlan entities.
 */
public interface TestPlanRepositoryPort {

    TestPlan save(TestPlan testPlan);

    Optional<TestPlan> findById(UUID testPlanId);

    List<TestPlan> findAll();

    Page<TestPlan> findAll(Pageable pageable);

    List<TestPlan> findByProjectId(UUID projectId);

    Page<TestPlan> findByProjectId(UUID projectId, Pageable pageable);

    Page<TestPlan> findByProjectIdAndStatus(UUID projectId, TestPlanStatus status, Pageable pageable);

    Page<UserStory> findStoriesByTestPlanId(UUID testPlanId, Pageable pageable);

    void deleteById(UUID testPlanId);

    boolean existsById(UUID testPlanId);
}
