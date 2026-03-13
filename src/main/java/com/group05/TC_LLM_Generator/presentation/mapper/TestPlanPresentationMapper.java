package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestPlanRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestPlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for TestPlan presentation layer
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TestPlanPresentationMapper {

    /**
     * Map TestPlan to TestPlanResponse
     */
    @Mapping(target = "projectId", source = "project.projectId")
    @Mapping(target = "createdByUserId", source = "createdByUser.userId")
    @Mapping(target = "createdByUserFullName", source = "createdByUser.fullName")
    @Mapping(target = "storyIds", source = "userStories", qualifiedByName = "storiesToIds")
    TestPlanResponse toResponse(TestPlan entity);

    /**
     * Map list of TestPlan to list of TestPlanResponse
     */
    List<TestPlanResponse> toResponseList(List<TestPlan> entities);

    /**
     * Update TestPlan from UpdateTestPlanRequest (name, description, status only)
     */
    @Mapping(target = "testPlanId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userStories", ignore = true)
    void updateEntity(UpdateTestPlanRequest request, @MappingTarget TestPlan entity);

    @Named("storiesToIds")
    default List<UUID> storiesToIds(List<UserStory> stories) {
        if (stories == null) return List.of();
        return stories.stream()
                .map(UserStory::getUserStoryId)
                .collect(Collectors.toList());
    }
}
