package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.controller.UserStoryController;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserStoryResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.UserStoryPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * HATEOAS assembler for UserStory resources
 */
@Component
@RequiredArgsConstructor
public class UserStoryModelAssembler implements RepresentationModelAssembler<UserStory, UserStoryResponse> {

    private final UserStoryPresentationMapper mapper;

    @Override
    public UserStoryResponse toModel(UserStory entity) {
        UserStoryResponse response = mapper.toResponse(entity);

        response.add(linkTo(methodOn(UserStoryController.class).getUserStoryById(entity.getUserStoryId())).withSelfRel());
        response.add(linkTo(methodOn(UserStoryController.class).updateUserStory(entity.getUserStoryId(), null)).withRel("update"));
        response.add(linkTo(methodOn(UserStoryController.class).deleteUserStory(entity.getUserStoryId())).withRel("delete"));
        response.add(linkTo(methodOn(UserStoryController.class).getAllUserStories(null)).withRel("userStories"));

        return response;
    }

    @Override
    public CollectionModel<UserStoryResponse> toCollectionModel(Iterable<? extends UserStory> entities) {
        CollectionModel<UserStoryResponse> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(UserStoryController.class).getAllUserStories(null)).withSelfRel());

        return collectionModel;
    }
}
