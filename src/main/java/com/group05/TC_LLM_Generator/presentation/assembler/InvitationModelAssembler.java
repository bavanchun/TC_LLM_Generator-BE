package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceInvitation;
import com.group05.TC_LLM_Generator.presentation.controller.WorkspaceInvitationController;
import com.group05.TC_LLM_Generator.presentation.dto.response.InvitationResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.InvitationPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class InvitationModelAssembler implements RepresentationModelAssembler<WorkspaceInvitation, InvitationResponse> {

    private final InvitationPresentationMapper mapper;

    @Override
    public InvitationResponse toModel(WorkspaceInvitation entity) {
        InvitationResponse response = mapper.toResponse(entity);

        response.add(linkTo(methodOn(WorkspaceInvitationController.class)
                .cancelInvitation(null, entity.getInvitationId())).withRel("cancel"));

        return response;
    }

    @Override
    public CollectionModel<InvitationResponse> toCollectionModel(Iterable<? extends WorkspaceInvitation> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
