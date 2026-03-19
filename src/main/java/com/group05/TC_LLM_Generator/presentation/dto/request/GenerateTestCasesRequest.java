package com.group05.TC_LLM_Generator.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for AI test case generation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTestCasesRequest {

    /**
     * Types of test cases to generate.
     * Defaults to ["Positive", "Negative", "Boundary"] if null/empty.
     */
    private List<String> testCaseTypes;
}
