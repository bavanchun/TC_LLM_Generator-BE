package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.ProjectService;
import com.group05.TC_LLM_Generator.application.service.TestPlanService;
import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.UserStoryService;
import com.group05.TC_LLM_Generator.domain.model.enums.TestPlanStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.assembler.TestPlanModelAssembler;
import com.group05.TC_LLM_Generator.presentation.assembler.UserStoryModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestPlanRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestPlanRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestPlanStatusRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestPlanResponse;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserStoryResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/test-plans")
@RequiredArgsConstructor
public class TestPlanController {

    private final TestPlanService testPlanService;
    private final ProjectService projectService;
    private final UserService userService;
    private final UserStoryService userStoryService;
    private final TestPlanPresentationMapper mapper;
    private final TestPlanModelAssembler assembler;
    private final UserStoryModelAssembler userStoryAssembler;
    private final PagedResourcesAssembler<TestPlan> pagedResourcesAssembler;
    private final PagedResourcesAssembler<UserStory> userStoryPagedAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<TestPlanResponse>> createTestPlan(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateTestPlanRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        Project project = projectService.getProjectById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        UserEntity creator = userService.getUserById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        TestPlanStatus status = TestPlanStatus.DRAFT;
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                status = TestPlanStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // fallback to DRAFT
            }
        }

        TestPlan testPlan = TestPlan.builder()
                .project(project)
                .createdByUser(creator)
                .name(request.getName())
                .description(request.getDescription())
                .status(status)
                .build();

        List<UserStory> stories = resolveStories(request.getStoryIds());

        TestPlan saved = testPlanService.createTestPlan(testPlan, stories, currentUserId.toString());
        TestPlanResponse response = assembler.toModel(saved);

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
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTestPlanRequest request) {

        String currentUserId = jwt.getSubject();

        TestPlan existing = testPlanService.getTestPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", id));

        mapper.updateEntity(request, existing);

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                existing.setStatus(TestPlanStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                // Keep existing status
            }
        }

        List<UserStory> newStories = request.getStoryIds() != null
                ? resolveStories(request.getStoryIds())
                : null;

        TestPlan updated = testPlanService.updateTestPlan(id, existing, newStories, currentUserId);
        TestPlanResponse response = assembler.toModel(updated);

        return ResponseEntity.ok(ApiResponse.success(response, "Test plan updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TestPlanResponse>> updateTestPlanStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTestPlanStatusRequest request) {

        String currentUserId = jwt.getSubject();

        TestPlan existing = testPlanService.getTestPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", id));

        try {
            existing.setStatus(TestPlanStatus.valueOf(request.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: " + request.getStatus() + ". Allowed: DRAFT, IN_PROGRESS, COMPLETED");
        }

        TestPlan updated = testPlanService.updateTestPlan(id, existing, null, currentUserId);
        TestPlanResponse response = assembler.toModel(updated);

        return ResponseEntity.ok(ApiResponse.success(response, "Test plan status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestPlan(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        String currentUserId = jwt.getSubject();

        if (!testPlanService.testPlanExists(id)) {
            throw new ResourceNotFoundException("TestPlan", "id", id);
        }

        testPlanService.deleteTestPlan(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Test plan deleted successfully"));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<PagedModel<TestPlanResponse>>> getTestPlansByProject(
            @PathVariable("projectId") UUID projectId,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestPlan> page;

        if (status != null && !status.isBlank()) {
            try {
                TestPlanStatus statusEnum = TestPlanStatus.valueOf(status.toUpperCase());
                page = testPlanService.getTestPlansByProjectAndStatus(projectId, statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                page = testPlanService.getTestPlansByProject(projectId, pageable);
            }
        } else {
            page = testPlanService.getTestPlansByProject(projectId, pageable);
        }

        PagedModel<TestPlanResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);
        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test plans retrieved successfully"));
    }

    @GetMapping("/{id}/stories")
    public ResponseEntity<ApiResponse<PagedModel<UserStoryResponse>>> getTestPlanStories(
            @PathVariable("id") UUID id,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        if (!testPlanService.testPlanExists(id)) {
            throw new ResourceNotFoundException("TestPlan", "id", id);
        }

        Page<UserStory> page = testPlanService.getStoriesByTestPlanId(id, pageable);
        PagedModel<UserStoryResponse> pagedModel = userStoryPagedAssembler.toModel(page, userStoryAssembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test plan stories retrieved successfully"));
    }

    // ---- helpers ----

    private List<UserStory> resolveStories(List<UUID> storyIds) {
        if (storyIds == null || storyIds.isEmpty()) return Collections.emptyList();
        return storyIds.stream()
                .map(storyId -> userStoryService.getUserStoryById(storyId)
                        .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", storyId)))
                .toList();
    }
}
