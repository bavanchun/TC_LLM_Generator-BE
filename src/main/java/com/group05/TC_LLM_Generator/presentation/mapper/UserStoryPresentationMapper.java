package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateUserStoryRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateUserStoryRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserStoryResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for UserStory presentation layer
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AcceptanceCriteriaPresentationMapper.class})
public interface UserStoryPresentationMapper {

    /**
     * Map UserStory to UserStoryResponse
     */
    @Mapping(target = "projectId", source = "project.projectId")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "acceptanceCriteria", source = "acceptanceCriteria")
    @Mapping(target = "iWantTo", source = "IWantTo")
    UserStoryResponse toResponse(UserStory entity);

    /**
     * Map list of UserStory to list of UserStoryResponse
     */
    List<UserStoryResponse> toResponseList(List<UserStory> entities);

    /**
     * Map CreateUserStoryRequest to UserStory
     */
    @Mapping(target = "userStoryId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "jiraIssueKey", ignore = true)
    @Mapping(target = "jiraIssueId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "acceptanceCriteria", ignore = true)
    @Mapping(target = "iWantTo", source = "IWantTo")
    UserStory toEntity(CreateUserStoryRequest request);

    /**
     * Update UserStory from UpdateUserStoryRequest
     */
    @Mapping(target = "userStoryId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "jiraIssueKey", ignore = true)
    @Mapping(target = "jiraIssueId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "acceptanceCriteria", ignore = true)
    void updateEntity(UpdateUserStoryRequest request, @MappingTarget UserStory entity);

    /**
     * Handle iWantTo field mapping after main mapping (JavaBeans naming convention workaround)
     */
    @AfterMapping
    default void mapIWantTo(UpdateUserStoryRequest request, @MappingTarget UserStory entity) {
        if (request.getIWantTo() != null) {
            entity.setIWantTo(request.getIWantTo());
        }
    }
}
