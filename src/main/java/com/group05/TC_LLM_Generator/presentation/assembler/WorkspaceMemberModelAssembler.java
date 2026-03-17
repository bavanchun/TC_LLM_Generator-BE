package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import com.group05.TC_LLM_Generator.presentation.controller.WorkspaceMemberController;
import com.group05.TC_LLM_Generator.presentation.dto.response.WorkspaceMemberResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.WorkspaceMemberPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class WorkspaceMemberModelAssembler implements RepresentationModelAssembler<WorkspaceMember, WorkspaceMemberResponse> {

    private final WorkspaceMemberPresentationMapper mapper;

    @Override
    public WorkspaceMemberResponse toModel(WorkspaceMember entity) {
        WorkspaceMemberResponse response = mapper.toResponse(entity);

        response.add(linkTo(methodOn(WorkspaceMemberController.class)
                .getWorkspaceMemberById(null, entity.getWorkspaceMemberId())).withSelfRel());
        response.add(linkTo(methodOn(WorkspaceMemberController.class)
                .removeWorkspaceMember(null, entity.getWorkspaceMemberId())).withRel("delete"));

        return response;
    }

    @Override
    public CollectionModel<WorkspaceMemberResponse> toCollectionModel(Iterable<? extends WorkspaceMember> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
