package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.AcceptanceCriteriaService;
import com.group05.TC_LLM_Generator.application.service.TestCaseService;
import com.group05.TC_LLM_Generator.application.service.UserStoryService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.assembler.TestCaseModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestCaseRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestCaseRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestCaseResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.TestCasePresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test-cases")
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;
    private final UserStoryService userStoryService;
    private final AcceptanceCriteriaService acceptanceCriteriaService;
    private final TestCasePresentationMapper mapper;
    private final TestCaseModelAssembler assembler;
    private final PagedResourcesAssembler<TestCase> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<TestCaseResponse>> createTestCase(
            @Valid @RequestBody CreateTestCaseRequest request) {

        // Enforce: must provide userStoryId or acceptanceCriteriaId
        if (request.getUserStoryId() == null && request.getAcceptanceCriteriaId() == null) {
            throw new IllegalArgumentException(
                    "Must provide at least userStoryId or acceptanceCriteriaId to link test case to a user story");
        }

        TestCase testCase = mapper.toEntity(request);

        if (request.getAcceptanceCriteriaId() != null) {
            // Load AC with its UserStory (lazy init inside service)
            AcceptanceCriteria ac = acceptanceCriteriaService.getAcceptanceCriteriaWithUserStory(
                            request.getAcceptanceCriteriaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "AcceptanceCriteria", "id", request.getAcceptanceCriteriaId()));
            testCase.setAcceptanceCriteria(ac);

            if (request.getUserStoryId() != null) {
                // Both provided — validate AC belongs to the given story
                if (!ac.getUserStory().getUserStoryId().equals(request.getUserStoryId())) {
                    throw new IllegalArgumentException(
                            "AcceptanceCriteria " + request.getAcceptanceCriteriaId()
                            + " does not belong to UserStory " + request.getUserStoryId());
                }
                testCase.setUserStory(ac.getUserStory());
            } else {
                // Only AC provided — auto-resolve UserStory from AC
                testCase.setUserStory(ac.getUserStory());
            }
        } else {
            // Only userStoryId provided
            UserStory userStory = userStoryService.getUserStoryById(request.getUserStoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "UserStory", "id", request.getUserStoryId()));
            testCase.setUserStory(userStory);
        }

        TestCase savedTestCase = testCaseService.createTestCase(testCase);
        TestCaseResponse response = assembler.toModel(savedTestCase);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Test case created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCaseResponse>> getTestCaseById(@PathVariable("id") UUID id) {
        TestCase testCase = testCaseService.getTestCaseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));

        TestCaseResponse response = assembler.toModel(testCase);

        return ResponseEntity.ok(ApiResponse.success(response, "Test case retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<TestCaseResponse>>> getAllTestCases(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestCase> page = testCaseService.getAllTestCases(pageable);
        PagedModel<TestCaseResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test cases retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCaseResponse>> updateTestCase(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTestCaseRequest request) {

        TestCase existingTestCase = testCaseService.getTestCaseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));

        mapper.updateEntity(request, existingTestCase);
        TestCase updatedTestCase = testCaseService.updateTestCase(id, existingTestCase);
        TestCaseResponse response = assembler.toModel(updatedTestCase);

        return ResponseEntity.ok(ApiResponse.success(response, "Test case updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestCase(@PathVariable("id") UUID id) {
        if (!testCaseService.testCaseExists(id)) {
            throw new ResourceNotFoundException("TestCase", "id", id);
        }

        testCaseService.deleteTestCase(id);

        return ResponseEntity.ok(ApiResponse.success("Test case deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedModel<TestCaseResponse>>> searchTestCases(
            @RequestParam("title") String title,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestCase> page = testCaseService.searchTestCasesByTitle(title, pageable);
        PagedModel<TestCaseResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test cases retrieved successfully"));
    }

    @GetMapping("/acceptance-criteria/{acceptanceCriteriaId}")
    public ResponseEntity<ApiResponse<PagedModel<TestCaseResponse>>> getTestCasesByAcceptanceCriteria(
            @PathVariable("acceptanceCriteriaId") UUID acceptanceCriteriaId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestCase> page = testCaseService.getTestCasesByAcceptanceCriteria(acceptanceCriteriaId, pageable);
        PagedModel<TestCaseResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test cases retrieved successfully"));
    }

    @GetMapping("/user-story/{userStoryId}")
    public ResponseEntity<ApiResponse<PagedModel<TestCaseResponse>>> getTestCasesByUserStory(
            @PathVariable("userStoryId") UUID userStoryId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestCase> page = testCaseService.getTestCasesByUserStory(userStoryId, pageable);
        PagedModel<TestCaseResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test cases retrieved successfully"));
    }
}
