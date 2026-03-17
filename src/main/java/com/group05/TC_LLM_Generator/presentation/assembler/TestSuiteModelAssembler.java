package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import com.group05.TC_LLM_Generator.presentation.controller.TestSuiteController;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestSuiteResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.TestSuitePresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class TestSuiteModelAssembler implements RepresentationModelAssembler<TestSuite, TestSuiteResponse> {

    private final TestSuitePresentationMapper mapper;

    @Override
    public TestSuiteResponse toModel(TestSuite entity) {
        TestSuiteResponse response = mapper.toResponse(entity);

        response.add(linkTo(methodOn(TestSuiteController.class).getTestSuiteById(null, entity.getTestSuiteId())).withSelfRel());
        response.add(linkTo(methodOn(TestSuiteController.class).updateTestSuite(null, entity.getTestSuiteId(), null)).withRel("update"));
        response.add(linkTo(methodOn(TestSuiteController.class).deleteTestSuite(null, entity.getTestSuiteId())).withRel("delete"));
        response.add(linkTo(methodOn(TestSuiteController.class).getAllTestSuites(null)).withRel("testSuites"));

        return response;
    }

    @Override
    public CollectionModel<TestSuiteResponse> toCollectionModel(Iterable<? extends TestSuite> entities) {
        CollectionModel<TestSuiteResponse> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(TestSuiteController.class).getAllTestSuites(null)).withSelfRel());

        return collectionModel;
    }
}
