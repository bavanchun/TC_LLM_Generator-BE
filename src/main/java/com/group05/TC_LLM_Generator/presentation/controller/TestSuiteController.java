package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.ProjectService;
import com.group05.TC_LLM_Generator.application.service.TestSuiteService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import com.group05.TC_LLM_Generator.presentation.assembler.TestSuiteModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestSuiteRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestSuiteRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestSuiteResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.TestSuitePresentationMapper;
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
@RequestMapping("/api/v1/test-suites")
@RequiredArgsConstructor
public class TestSuiteController {

    private final TestSuiteService testSuiteService;
    private final ProjectService projectService;
    private final TestSuitePresentationMapper mapper;
    private final TestSuiteModelAssembler assembler;
    private final PagedResourcesAssembler<TestSuite> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<TestSuiteResponse>> createTestSuite(
            @Valid @RequestBody CreateTestSuiteRequest request) {

        Project project = projectService.getProjectById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        TestSuite testSuite = mapper.toEntity(request);
        testSuite.setProject(project);

        TestSuite savedTestSuite = testSuiteService.createTestSuite(testSuite);
        TestSuiteResponse response = assembler.toModel(savedTestSuite);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Test suite created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestSuiteResponse>> getTestSuiteById(@PathVariable("id") UUID id) {
        TestSuite testSuite = testSuiteService.getTestSuiteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", id));

        TestSuiteResponse response = assembler.toModel(testSuite);

        return ResponseEntity.ok(ApiResponse.success(response, "Test suite retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<TestSuiteResponse>>> getAllTestSuites(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestSuite> page = testSuiteService.getAllTestSuites(pageable);
        PagedModel<TestSuiteResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test suites retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestSuiteResponse>> updateTestSuite(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTestSuiteRequest request) {

        TestSuite existingTestSuite = testSuiteService.getTestSuiteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", id));

        mapper.updateEntity(request, existingTestSuite);
        TestSuite updatedTestSuite = testSuiteService.updateTestSuite(id, existingTestSuite);
        TestSuiteResponse response = assembler.toModel(updatedTestSuite);

        return ResponseEntity.ok(ApiResponse.success(response, "Test suite updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestSuite(@PathVariable("id") UUID id) {
        if (!testSuiteService.testSuiteExists(id)) {
            throw new ResourceNotFoundException("TestSuite", "id", id);
        }

        testSuiteService.deleteTestSuite(id);

        return ResponseEntity.ok(ApiResponse.success("Test suite deleted successfully"));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<PagedModel<TestSuiteResponse>>> getTestSuitesByProject(
            @PathVariable("projectId") UUID projectId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestSuite> page = testSuiteService.getTestSuitesByProject(projectId, pageable);
        PagedModel<TestSuiteResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test suites retrieved successfully"));
    }
}
