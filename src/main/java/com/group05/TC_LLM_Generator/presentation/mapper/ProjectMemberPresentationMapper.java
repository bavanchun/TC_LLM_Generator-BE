package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateProjectMemberRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.ProjectMemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMemberPresentationMapper {

    @Mapping(target = "projectId", source = "project.projectId")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userFullName", source = "user.fullName")
    @Mapping(target = "userEmail", source = "user.email")
    ProjectMemberResponse toResponse(ProjectMember entity);

    List<ProjectMemberResponse> toResponseList(List<ProjectMember> entities);

    @Mapping(target = "projectMemberId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    void updateEntity(UpdateProjectMemberRequest request, @MappingTarget ProjectMember entity);
}
