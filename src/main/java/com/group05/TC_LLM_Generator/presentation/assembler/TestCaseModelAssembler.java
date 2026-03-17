package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.presentation.controller.TestCaseController;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestCaseResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.TestCasePresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * HATEOAS assembler for TestCase resources
 */
@Component
@RequiredArgsConstructor
public class TestCaseModelAssembler implements RepresentationModelAssembler<TestCase, TestCaseResponse> {

    private final TestCasePresentationMapper mapper;

    @Override
    public TestCaseResponse toModel(TestCase entity) {
        TestCaseResponse response = mapper.toResponse(entity);

        // Add HATEOAS links
        response.add(linkTo(methodOn(TestCaseController.class).getTestCaseById(null, entity.getTestCaseId())).withSelfRel());
        response.add(linkTo(methodOn(TestCaseController.class).updateTestCase(null, entity.getTestCaseId(), null)).withRel("update"));
        response.add(linkTo(methodOn(TestCaseController.class).deleteTestCase(null, entity.getTestCaseId())).withRel("delete"));
        response.add(linkTo(methodOn(TestCaseController.class).getAllTestCases(null)).withRel("testCases"));

        return response;
    }

    @Override
    public CollectionModel<TestCaseResponse> toCollectionModel(Iterable<? extends TestCase> entities) {
        CollectionModel<TestCaseResponse> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(TestCaseController.class).getAllTestCases(null)).withSelfRel());

        return collectionModel;
    }
}
