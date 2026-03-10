package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.ProjectService;
import com.group05.TC_LLM_Generator.application.service.TestPlanService;
import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.presentation.assembler.TestPlanModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestPlanRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestPlanRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestPlanResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.TestPlanPresentationMapper;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test-plans")
@RequiredArgsConstructor
public class TestPlanController {

    private final TestPlanService testPlanService;
    private final ProjectService projectService;
    private final UserService userService;
    private final TestPlanPresentationMapper mapper;
    private final TestPlanModelAssembler assembler;
    private final PagedResourcesAssembler<TestPlan> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<TestPlanResponse>> createTestPlan(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateTestPlanRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        Project project = projectService.getProjectById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        UserEntity creator = userService.getUserById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        TestPlan testPlan = TestPlan.builder()
                .project(project)
                .createdByUser(creator)
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        TestPlan savedTestPlan = testPlanService.createTestPlan(testPlan);
        TestPlanResponse response = assembler.toModel(savedTestPlan);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Test plan created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestPlanResponse>> getTestPlanById(@PathVariable("id") UUID id) {
        TestPlan testPlan = testPlanService.getTestPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", id));

        TestPlanResponse response = assembler.toModel(testPlan);
        return ResponseEntity.ok(ApiResponse.success(response, "Test plan retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<TestPlanResponse>>> getAllTestPlans(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestPlan> page = testPlanService.getAllTestPlans(pageable);
        PagedModel<TestPlanResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test plans retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestPlanResponse>> updateTestPlan(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTestPlanRequest request) {

        TestPlan existingTestPlan = testPlanService.getTestPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", id));

        mapper.updateEntity(request, existingTestPlan);
        TestPlan updatedTestPlan = testPlanService.updateTestPlan(id, existingTestPlan);
        TestPlanResponse response = assembler.toModel(updatedTestPlan);

        return ResponseEntity.ok(ApiResponse.success(response, "Test plan updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestPlan(@PathVariable("id") UUID id) {
        if (!testPlanService.testPlanExists(id)) {
            throw new ResourceNotFoundException("TestPlan", "id", id);
        }

        testPlanService.deleteTestPlan(id);
        return ResponseEntity.ok(ApiResponse.success("Test plan deleted successfully"));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<PagedModel<TestPlanResponse>>> getTestPlansByProject(
            @PathVariable("projectId") UUID projectId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestPlan> page = testPlanService.getTestPlansByProject(projectId, pageable);
        PagedModel<TestPlanResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test plans retrieved successfully"));
    }
}
