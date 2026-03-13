package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.TestPlanRepositoryPort;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.Action;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.EntityType;
import com.group05.TC_LLM_Generator.domain.model.enums.TestPlanStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for TestPlan entity
 * Handles CRUD operations and test plan-related use cases
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestPlanService {

    private final TestPlanRepositoryPort testPlanRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Create a new test plan with linked user stories
     */
    @Transactional
    public TestPlan createTestPlan(TestPlan testPlan, List<UserStory> stories, String performedByUserId) {
        if (stories != null && !stories.isEmpty()) {
            testPlan.getUserStories().addAll(stories);
        }

        TestPlan saved = testPlanRepository.save(testPlan);

        Hibernate.initialize(saved.getProject());
        Hibernate.initialize(saved.getCreatedByUser());
        Hibernate.initialize(saved.getUserStories());

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.TEST_PLAN, Action.CREATED,
                saved.getTestPlanId().toString(),
                saved.getProject().getProjectId().toString(),
                null,
                performedByUserId
        ));

        return saved;
    }

    /**
     * Get test plan by ID
     */
    public Optional<TestPlan> getTestPlanById(UUID testPlanId) {
        Optional<TestPlan> opt = testPlanRepository.findById(testPlanId);
        opt.ifPresent(plan -> {
            Hibernate.initialize(plan.getProject());
            Hibernate.initialize(plan.getCreatedByUser());
            Hibernate.initialize(plan.getUserStories());
        });
        return opt;
    }

    /**
     * Get all test plans with pagination
     */
    public Page<TestPlan> getAllTestPlans(Pageable pageable) {
        Page<TestPlan> page = testPlanRepository.findAll(pageable);
        page.forEach(plan -> {
            Hibernate.initialize(plan.getProject());
            Hibernate.initialize(plan.getCreatedByUser());
            Hibernate.initialize(plan.getUserStories());
        });
        return page;
    }

    /**
     * Get test plans by project ID with pagination
     */
    public Page<TestPlan> getTestPlansByProject(UUID projectId, Pageable pageable) {
        Page<TestPlan> page = testPlanRepository.findByProjectId(projectId, pageable);
        page.forEach(plan -> {
            Hibernate.initialize(plan.getProject());
            Hibernate.initialize(plan.getCreatedByUser());
            Hibernate.initialize(plan.getUserStories());
        });
        return page;
    }

    /**
     * Get test plans by project ID and status with pagination
     */
    public Page<TestPlan> getTestPlansByProjectAndStatus(UUID projectId, TestPlanStatus status, Pageable pageable) {
        Page<TestPlan> page = testPlanRepository.findByProjectIdAndStatus(projectId, status, pageable);
        page.forEach(plan -> {
            Hibernate.initialize(plan.getProject());
            Hibernate.initialize(plan.getCreatedByUser());
            Hibernate.initialize(plan.getUserStories());
        });
        return page;
    }

    /**
     * Update test plan fields and optionally replace linked stories
     */
    @Transactional
    public TestPlan updateTestPlan(UUID testPlanId, TestPlan updatedTestPlan, List<UserStory> newStories, String performedByUserId) {
        TestPlan existing = testPlanRepository.findById(testPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Test plan not found: " + testPlanId));

        if (updatedTestPlan.getName() != null) {
            existing.setName(updatedTestPlan.getName());
        }
        if (updatedTestPlan.getDescription() != null) {
            existing.setDescription(updatedTestPlan.getDescription());
        }
        if (updatedTestPlan.getStatus() != null) {
            existing.setStatus(updatedTestPlan.getStatus());
        }
        if (newStories != null) {
            existing.getUserStories().clear();
            existing.getUserStories().addAll(newStories);
        }

        TestPlan saved = testPlanRepository.save(existing);

        Hibernate.initialize(saved.getProject());
        Hibernate.initialize(saved.getCreatedByUser());
        Hibernate.initialize(saved.getUserStories());

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.TEST_PLAN, Action.UPDATED,
                saved.getTestPlanId().toString(),
                saved.getProject().getProjectId().toString(),
                null,
                performedByUserId
        ));

        return saved;
    }

    /**
     * Delete test plan by ID
     */
    @Transactional
    public void deleteTestPlan(UUID testPlanId, String performedByUserId) {
        TestPlan existing = testPlanRepository.findById(testPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Test plan not found: " + testPlanId));

        String projectId = existing.getProject().getProjectId().toString();

        testPlanRepository.deleteById(testPlanId);

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.TEST_PLAN, Action.DELETED,
                testPlanId.toString(),
                projectId,
                null,
                performedByUserId
        ));
    }

    /**
     * Get user stories linked to a test plan (with lazy fields initialized within transaction)
     */
    public Page<UserStory> getStoriesByTestPlanId(UUID testPlanId, Pageable pageable) {
        Page<UserStory> page = testPlanRepository.findStoriesByTestPlanId(testPlanId, pageable);
        page.forEach(story -> {
            Hibernate.initialize(story.getProject());
            Hibernate.initialize(story.getAcceptanceCriteria());
        });
        return page;
    }

    /**
     * Check if test plan exists
     */
    public boolean testPlanExists(UUID testPlanId) {
        return testPlanRepository.existsById(testPlanId);
    }
}
