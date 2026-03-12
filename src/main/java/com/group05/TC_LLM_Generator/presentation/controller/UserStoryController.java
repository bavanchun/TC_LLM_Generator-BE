package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.ProjectService;
import com.group05.TC_LLM_Generator.application.service.UserStoryService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.assembler.UserStoryModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateUserStoryRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateUserStoryRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserStoryResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.AcceptanceCriteriaPresentationMapper;
import com.group05.TC_LLM_Generator.presentation.mapper.UserStoryPresentationMapper;
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

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for UserStory CRUD operations.
 * Implements HATEOAS Level 3 REST API with wrapped responses and pagination.
 */
@RestController
@RequestMapping("/api/v1/user-stories")
@RequiredArgsConstructor
public class UserStoryController {

    private final UserStoryService userStoryService;
    private final ProjectService projectService;
    private final UserStoryPresentationMapper mapper;
    private final AcceptanceCriteriaPresentationMapper acMapper;
    private final UserStoryModelAssembler assembler;
    private final PagedResourcesAssembler<UserStory> pagedResourcesAssembler;

    /**
     * Create a new user story (with optional acceptance criteria in a single request)
     * POST /api/v1/user-stories
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserStoryResponse>> createUserStory(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateUserStoryRequest request) {

        String currentUserId = jwt.getSubject();

        Project project = projectService.getProjectById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        UserStory userStory = mapper.toEntity(request);
        userStory.setProject(project);

        // Map AC request DTOs to entities
        List<AcceptanceCriteria> acList = null;
        if (request.getAcceptanceCriteria() != null && !request.getAcceptanceCriteria().isEmpty()) {
            acList = acMapper.toEntityList(request.getAcceptanceCriteria());
        }

        UserStory savedUserStory = userStoryService.createUserStory(userStory, acList, currentUserId);
        UserStoryResponse response = assembler.toModel(savedUserStory);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User story created successfully"));
    }

    /**
     * Get user story by ID
     * GET /api/v1/user-stories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserStoryResponse>> getUserStoryById(@PathVariable("id") UUID id) {
        UserStory userStory = userStoryService.getUserStoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", id));

        UserStoryResponse response = assembler.toModel(userStory);

        return ResponseEntity.ok(ApiResponse.success(response, "User story retrieved successfully"));
    }

    /**
     * Get all user stories with pagination
     * GET /api/v1/user-stories?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<UserStoryResponse>>> getAllUserStories(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserStory> page = userStoryService.getAllUserStories(pageable);
        PagedModel<UserStoryResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "User stories retrieved successfully"));
    }

    /**
     * Update user story by ID (with optional AC replacement)
     * PUT /api/v1/user-stories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserStoryResponse>> updateUserStory(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserStoryRequest request) {

        String currentUserId = jwt.getSubject();

        UserStory existingUserStory = userStoryService.getUserStoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", id));

        mapper.updateEntity(request, existingUserStory);

        // Map AC request DTOs to entities if provided
        List<AcceptanceCriteria> acList = null;
        if (request.getAcceptanceCriteria() != null) {
            acList = acMapper.toEntityList(request.getAcceptanceCriteria());
        }

        UserStory updatedUserStory = userStoryService.updateUserStory(id, existingUserStory, acList, currentUserId);
        UserStoryResponse response = assembler.toModel(updatedUserStory);

        return ResponseEntity.ok(ApiResponse.success(response, "User story updated successfully"));
    }

    /**
     * Delete user story by ID
     * DELETE /api/v1/user-stories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserStory(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt) {
            
        String currentUserId = jwt.getSubject();
        
        if (!userStoryService.userStoryExists(id)) {
            throw new ResourceNotFoundException("UserStory", "id", id);
        }

        userStoryService.deleteUserStory(id, currentUserId);

        return ResponseEntity.ok(ApiResponse.success("User story deleted successfully"));
    }

    /**
     * Get user stories by project ID with pagination
     * GET /api/v1/user-stories/project/{projectId}?page=0&size=20
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<PagedModel<UserStoryResponse>>> getUserStoriesByProject(
            @PathVariable("projectId") UUID projectId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserStory> page = userStoryService.getUserStoriesByProject(projectId, pageable);
        PagedModel<UserStoryResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "User stories retrieved successfully"));
    }
}
