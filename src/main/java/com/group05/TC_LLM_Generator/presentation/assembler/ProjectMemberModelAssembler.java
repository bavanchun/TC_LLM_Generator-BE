package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import com.group05.TC_LLM_Generator.presentation.controller.ProjectMemberController;
import com.group05.TC_LLM_Generator.presentation.dto.response.ProjectMemberResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.ProjectMemberPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class ProjectMemberModelAssembler implements RepresentationModelAssembler<ProjectMember, ProjectMemberResponse> {

    private final ProjectMemberPresentationMapper mapper;

    @Override
    public ProjectMemberResponse toModel(ProjectMember entity) {
        ProjectMemberResponse response = mapper.toResponse(entity);

        response.add(linkTo(methodOn(ProjectMemberController.class).getProjectMemberById(entity.getProjectMemberId())).withSelfRel());
        response.add(linkTo(methodOn(ProjectMemberController.class).removeProjectMember(entity.getProjectMemberId())).withRel("delete"));
        response.add(linkTo(methodOn(ProjectMemberController.class).getAllProjectMembers(null)).withRel("projectMembers"));

        return response;
    }

    @Override
    public CollectionModel<ProjectMemberResponse> toCollectionModel(Iterable<? extends ProjectMember> entities) {
        CollectionModel<ProjectMemberResponse> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(ProjectMemberController.class).getAllProjectMembers(null)).withSelfRel());

        return collectionModel;
    }
}
