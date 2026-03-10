package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.presentation.controller.WorkspaceController;
import com.group05.TC_LLM_Generator.presentation.dto.response.WorkspaceResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.WorkspacePresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class WorkspaceModelAssembler implements RepresentationModelAssembler<Workspace, WorkspaceResponse> {

    private final WorkspacePresentationMapper mapper;

    @Override
    public WorkspaceResponse toModel(Workspace entity) {
        WorkspaceResponse response = mapper.toResponse(entity);

        response.add(linkTo(methodOn(WorkspaceController.class).getWorkspaceById(entity.getWorkspaceId())).withSelfRel());
        response.add(linkTo(methodOn(WorkspaceController.class).updateWorkspace(null, entity.getWorkspaceId(), null)).withRel("update"));
        response.add(linkTo(methodOn(WorkspaceController.class).deleteWorkspace(null, entity.getWorkspaceId())).withRel("delete"));
        response.add(linkTo(methodOn(WorkspaceController.class).getMyWorkspaces(null, null)).withRel("workspaces"));

        return response;
    }

    @Override
    public CollectionModel<WorkspaceResponse> toCollectionModel(Iterable<? extends Workspace> entities) {
        CollectionModel<WorkspaceResponse> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(WorkspaceController.class).getMyWorkspaces(null, null)).withSelfRel());
        return collectionModel;
    }
}
