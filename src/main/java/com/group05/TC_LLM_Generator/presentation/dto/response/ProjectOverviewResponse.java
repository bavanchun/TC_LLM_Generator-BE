package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for project-level overview / dashboard.
 * Aggregates all key metrics for a single project.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectOverviewResponse {

    // --- Stats Cards ---
    private long totalStories;
    private long totalTestCases;
    private long totalTestSuites;
    private long totalMembers;
    private long aiGeneratedTestCases;

    // --- Test Execution Status (for Doughnut chart) ---
    // Keys: PASSED, FAILED, NOT_RUN, IN_PROGRESS, etc.
    private Map<String, Long> testExecutionStatus;

    // --- Story Status Distribution (for Bar chart) ---
    // Keys: DRAFT, READY, IN_PROGRESS, DONE, ARCHIVED
    private Map<String, Long> storyStatusDistribution;

    // --- Test Coverage ---
    private long storiesWithTestCases;
    private long storiesWithoutTestCases;

    // --- Recent User Stories ---
    private List<RecentStory> recentStories;

    // --- Current Test Plan Summary ---
    private CurrentTestPlanSummary currentTestPlan;

    // --- Team Members ---
    private List<TeamMemberSummary> teamMembers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentStory {
        private String id;
        private String title;
        private String status;
        private int acceptanceCriteriaCount;
        private Instant createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentTestPlanSummary {
        private String id;
        private String name;
        private String status;
        private String description;
        private Instant createdAt;
        private long totalItems;
        private long passedCount;
        private long failedCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberSummary {
        private String id;
        private String fullName;
        private String role;
    }
}
