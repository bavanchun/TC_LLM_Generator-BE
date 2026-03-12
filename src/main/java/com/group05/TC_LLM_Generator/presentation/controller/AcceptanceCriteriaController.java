package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.AcceptanceCriteriaService;
import com.group05.TC_LLM_Generator.application.service.UserStoryService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.assembler.AcceptanceCriteriaModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateAcceptanceCriteriaRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateAcceptanceCriteriaRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.AcceptanceCriteriaResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.AcceptanceCriteriaPresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for AcceptanceCriteria CRUD operations.
 * Nested resource under user stories + individual CRUD.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AcceptanceCriteriaController {

    private final AcceptanceCriteriaService acceptanceCriteriaService;
    private final UserStoryService userStoryService;
    private final AcceptanceCriteriaPresentationMapper mapper;
    private final AcceptanceCriteriaModelAssembler assembler;

    /**
     * Get acceptance criteria by user story ID (ordered by orderNo)
     * GET /api/v1/user-stories/{storyId}/acceptance-criteria
     */
    @GetMapping("/user-stories/{storyId}/acceptance-criteria")
    public ResponseEntity<ApiResponse<CollectionModel<AcceptanceCriteriaResponse>>> getAcceptanceCriteriaByUserStory(
            @PathVariable("storyId") UUID storyId) {

        if (!userStoryService.userStoryExists(storyId)) {
            throw new ResourceNotFoundException("UserStory", "id", storyId);
        }

        List<AcceptanceCriteria> criteria = acceptanceCriteriaService.getByUserStoryIdOrdered(storyId);
        CollectionModel<AcceptanceCriteriaResponse> collectionModel = assembler.toCollectionModel(criteria);

        return ResponseEntity.ok(ApiResponse.success(collectionModel, "Acceptance criteria retrieved successfully"));
    }

    /**
     * Create acceptance criteria for a user story
     * POST /api/v1/user-stories/{storyId}/acceptance-criteria
     */
    @PostMapping("/user-stories/{storyId}/acceptance-criteria")
    public ResponseEntity<ApiResponse<AcceptanceCriteriaResponse>> createAcceptanceCriteria(
            @PathVariable("storyId") UUID storyId,
            @Valid @RequestBody CreateAcceptanceCriteriaRequest request) {

        UserStory userStory = userStoryService.getUserStoryById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", storyId));

        AcceptanceCriteria entity = mapper.toEntity(request);
        entity.setUserStory(userStory);
        AcceptanceCriteria saved = acceptanceCriteriaService.createAcceptanceCriteria(entity);
        AcceptanceCriteriaResponse response = assembler.toModel(saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Acceptance criteria created successfully"));
    }

    /**
     * Batch create acceptance criteria for a user story
     * POST /api/v1/user-stories/{storyId}/acceptance-criteria/batch
     */
    @PostMapping("/user-stories/{storyId}/acceptance-criteria/batch")
    public ResponseEntity<ApiResponse<CollectionModel<AcceptanceCriteriaResponse>>> batchCreateAcceptanceCriteria(
            @PathVariable("storyId") UUID storyId,
            @Valid @RequestBody List<CreateAcceptanceCriteriaRequest> requests) {

        UserStory userStory = userStoryService.getUserStoryById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", storyId));

        List<AcceptanceCriteria> entities = mapper.toEntityList(requests);
        entities.forEach(entity -> entity.setUserStory(userStory));
        List<AcceptanceCriteria> savedList = acceptanceCriteriaService.saveAll(entities);
        CollectionModel<AcceptanceCriteriaResponse> collectionModel = assembler.toCollectionModel(savedList);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(collectionModel, "Acceptance criteria batch created successfully"));
    }

    /**
     * Get acceptance criteria by ID
     * GET /api/v1/acceptance-criteria/{id}
     */
    @GetMapping("/acceptance-criteria/{id}")
    public ResponseEntity<ApiResponse<AcceptanceCriteriaResponse>> getAcceptanceCriteriaById(
            @PathVariable("id") UUID id) {

        AcceptanceCriteria criteria = acceptanceCriteriaService.getAcceptanceCriteriaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcceptanceCriteria", "id", id));

        AcceptanceCriteriaResponse response = assembler.toModel(criteria);

        return ResponseEntity.ok(ApiResponse.success(response, "Acceptance criteria retrieved successfully"));
    }

    /**
     * Update acceptance criteria by ID
     * PUT /api/v1/acceptance-criteria/{id}
     */
    @PutMapping("/acceptance-criteria/{id}")
    public ResponseEntity<ApiResponse<AcceptanceCriteriaResponse>> updateAcceptanceCriteria(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateAcceptanceCriteriaRequest request) {

        AcceptanceCriteria existing = acceptanceCriteriaService.getAcceptanceCriteriaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcceptanceCriteria", "id", id));

        mapper.updateEntity(request, existing);
        AcceptanceCriteria updated = acceptanceCriteriaService.updateAcceptanceCriteria(id, existing);
        AcceptanceCriteriaResponse response = assembler.toModel(updated);

        return ResponseEntity.ok(ApiResponse.success(response, "Acceptance criteria updated successfully"));
    }

    /**
     * Delete acceptance criteria by ID
     * DELETE /api/v1/acceptance-criteria/{id}
     */
    @DeleteMapping("/acceptance-criteria/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAcceptanceCriteria(@PathVariable("id") UUID id) {
        if (!acceptanceCriteriaService.existsById(id)) {
            throw new ResourceNotFoundException("AcceptanceCriteria", "id", id);
        }

        acceptanceCriteriaService.deleteAcceptanceCriteria(id);

        return ResponseEntity.ok(ApiResponse.success("Acceptance criteria deleted successfully"));
    }
}
