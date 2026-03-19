package com.group05.TC_LLM_Generator.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for AI-generated test cases
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateTestCasesResponse {
    private int generatedCount;
    private List<GeneratedTestCase> testCases;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedTestCase {
        private UUID testCaseId;
        private String title;
        private String type;
        private String preconditions;
        private String steps;
        private String expectedResult;
        private UUID acceptanceCriteriaId;
    }
}
