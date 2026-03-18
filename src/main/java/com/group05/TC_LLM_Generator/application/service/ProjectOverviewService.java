package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.*;
import com.group05.TC_LLM_Generator.domain.model.enums.StoryStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.*;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.TestPlanItemRepository;
import com.group05.TC_LLM_Generator.presentation.dto.response.ProjectOverviewResponse;
import com.group05.TC_LLM_Generator.presentation.dto.response.ProjectOverviewResponse.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for aggregating project-level overview / dashboard data.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectOverviewService {

    private final UserStoryRepositoryPort userStoryRepository;
    private final TestCaseRepositoryPort testCaseRepository;
    private final TestSuiteRepositoryPort testSuiteRepository;
    private final ProjectMemberRepositoryPort projectMemberRepository;
    private final TestPlanRepositoryPort testPlanRepository;
    private final TestPlanItemRepository testPlanItemRepository;

    public ProjectOverviewResponse getOverview(UUID projectId) {

        // 1. Stats Cards
        long totalStories = userStoryRepository.countByProjectId(projectId);
        long totalTestCases = testCaseRepository.countByProjectId(projectId);
        long totalTestSuites = testSuiteRepository.countByProjectId(projectId);
        long totalMembers = projectMemberRepository.countByProjectId(projectId);
        long aiGeneratedTestCases = testCaseRepository.countByProjectIdAndGeneratedByAi(projectId, true);

        // 2. Story Status Distribution
        Map<String, Long> storyStatusDistribution = new LinkedHashMap<>();
        for (StoryStatus status : StoryStatus.values()) {
            long count = userStoryRepository.countByProjectIdAndStatus(projectId, status.name());
            storyStatusDistribution.put(status.name(), count);
        }

        // 3. Test Execution Status (from latest test plan)
        Map<String, Long> testExecutionStatus = new LinkedHashMap<>();
        CurrentTestPlanSummary currentTestPlanSummary = null;

        Page<TestPlan> plansPage = testPlanRepository.findByProjectId(
                projectId, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")));

        if (!plansPage.isEmpty()) {
            TestPlan latestPlan = plansPage.getContent().get(0);
            List<TestPlanItem> items = testPlanItemRepository.findByTestPlan_TestPlanId(latestPlan.getTestPlanId());

            // Group items by status
            testExecutionStatus = items.stream()
                    .collect(Collectors.groupingBy(
                            TestPlanItem::getStatus,
                            LinkedHashMap::new,
                            Collectors.counting()));

            long passedCount = testExecutionStatus.getOrDefault("PASSED", 0L);
            long failedCount = testExecutionStatus.getOrDefault("FAILED", 0L);

            currentTestPlanSummary = CurrentTestPlanSummary.builder()
                    .id(latestPlan.getTestPlanId().toString())
                    .name(latestPlan.getName())
                    .status(latestPlan.getStatus() != null ? latestPlan.getStatus().name() : "DRAFT")
                    .description(latestPlan.getDescription())
                    .createdAt(latestPlan.getCreatedAt())
                    .totalItems(items.size())
                    .passedCount(passedCount)
                    .failedCount(failedCount)
                    .build();
        }

        // 4. Test Coverage
        List<UserStory> allStories = userStoryRepository.findByProjectId(projectId);
        long storiesWithTestCases = allStories.stream()
                .filter(s -> s.getTestCases() != null && !s.getTestCases().isEmpty())
                .count();
        long storiesWithoutTestCases = totalStories - storiesWithTestCases;

        // 5. Recent User Stories (latest 5)
        Page<UserStory> recentStoriesPage = userStoryRepository.findByProjectId(
                projectId, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<RecentStory> recentStories = recentStoriesPage.getContent().stream()
                .map(s -> RecentStory.builder()
                        .id(s.getUserStoryId().toString())
                        .title(s.getTitle())
                        .status(s.getStatus() != null ? s.getStatus().name() : "DRAFT")
                        .acceptanceCriteriaCount(
                                s.getAcceptanceCriteria() != null ? s.getAcceptanceCriteria().size() : 0)
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        // 6. Team Members (up to 10)
        Page<ProjectMember> membersPage = projectMemberRepository.findByProjectId(
                projectId, PageRequest.of(0, 10));
        List<TeamMemberSummary> teamMembers = membersPage.getContent().stream()
                .map(m -> TeamMemberSummary.builder()
                        .id(m.getProjectMemberId().toString())
                        .fullName(m.getUser() != null ? m.getUser().getFullName() : "Unknown")
                        .role(m.getRole() != null ? m.getRole() : "MEMBER")
                        .build())
                .collect(Collectors.toList());

        return ProjectOverviewResponse.builder()
                .totalStories(totalStories)
                .totalTestCases(totalTestCases)
                .totalTestSuites(totalTestSuites)
                .totalMembers(totalMembers)
                .aiGeneratedTestCases(aiGeneratedTestCases)
                .testExecutionStatus(testExecutionStatus)
                .storyStatusDistribution(storyStatusDistribution)
                .storiesWithTestCases(storiesWithTestCases)
                .storiesWithoutTestCases(storiesWithoutTestCases)
                .recentStories(recentStories)
                .currentTestPlan(currentTestPlanSummary)
                .teamMembers(teamMembers)
                .build();
    }
}
