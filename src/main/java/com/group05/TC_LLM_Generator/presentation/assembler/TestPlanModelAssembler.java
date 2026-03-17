package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.presentation.controller.TestPlanController;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestPlanResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.TestPlanPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * HATEOAS assembler for TestPlan resources
 */
@Component
@RequiredArgsConstructor
public class TestPlanModelAssembler implements RepresentationModelAssembler<TestPlan, TestPlanResponse> {

    private final TestPlanPresentationMapper mapper;

    @Override
    public TestPlanResponse toModel(TestPlan entity) {
        TestPlanResponse response = mapper.toResponse(entity);

        // Add HATEOAS links
        response.add(linkTo(methodOn(TestPlanController.class).getTestPlanById(null, entity.getTestPlanId())).withSelfRel());
        response.add(linkTo(methodOn(TestPlanController.class).updateTestPlan(null, entity.getTestPlanId(), null)).withRel("update"));
        response.add(linkTo(methodOn(TestPlanController.class).deleteTestPlan(null, entity.getTestPlanId())).withRel("delete"));
        response.add(linkTo(methodOn(TestPlanController.class).getAllTestPlans(null)).withRel("testPlans"));

        return response;
    }

    @Override
    public CollectionModel<TestPlanResponse> toCollectionModel(Iterable<? extends TestPlan> entities) {
        CollectionModel<TestPlanResponse> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(TestPlanController.class).getAllTestPlans(null)).withSelfRel());

        return collectionModel;
    }
}
