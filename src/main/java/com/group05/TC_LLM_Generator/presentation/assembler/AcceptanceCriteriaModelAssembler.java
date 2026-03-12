package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.presentation.controller.AcceptanceCriteriaController;
import com.group05.TC_LLM_Generator.presentation.dto.response.AcceptanceCriteriaResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.AcceptanceCriteriaPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * HATEOAS assembler for AcceptanceCriteria resources
 */
@Component
@RequiredArgsConstructor
public class AcceptanceCriteriaModelAssembler implements RepresentationModelAssembler<AcceptanceCriteria, AcceptanceCriteriaResponse> {

    private final AcceptanceCriteriaPresentationMapper mapper;

    @Override
    public AcceptanceCriteriaResponse toModel(AcceptanceCriteria entity) {
        AcceptanceCriteriaResponse response = mapper.toResponse(entity);

        response.add(linkTo(methodOn(AcceptanceCriteriaController.class)
                .getAcceptanceCriteriaById(entity.getAcceptanceCriteriaId())).withSelfRel());
        response.add(linkTo(methodOn(AcceptanceCriteriaController.class)
                .updateAcceptanceCriteria(entity.getAcceptanceCriteriaId(), null)).withRel("update"));
        response.add(linkTo(methodOn(AcceptanceCriteriaController.class)
                .deleteAcceptanceCriteria(entity.getAcceptanceCriteriaId())).withRel("delete"));
        response.add(linkTo(methodOn(AcceptanceCriteriaController.class)
                .getAcceptanceCriteriaByUserStory(entity.getUserStory().getUserStoryId())).withRel("acceptanceCriteria"));

        return response;
    }

    @Override
    public CollectionModel<AcceptanceCriteriaResponse> toCollectionModel(Iterable<? extends AcceptanceCriteria> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
