package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateWorkspaceRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.WorkspaceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Workspace presentation layer
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkspacePresentationMapper {

    /**
     * Map Workspace to WorkspaceResponse
     */
    @Mapping(target = "ownerUserId", source = "ownerUser.userId")
    @Mapping(target = "ownerFullName", source = "ownerUser.fullName")
    @Mapping(target = "projectCount", ignore = true)
    @Mapping(target = "memberCount", ignore = true)
    WorkspaceResponse toResponse(Workspace entity);

    /**
     * Map list of Workspace to list of WorkspaceResponse
     */
    List<WorkspaceResponse> toResponseList(List<Workspace> entities);

    /**
     * Update Workspace from UpdateWorkspaceRequest
     */
    @Mapping(target = "workspaceId", ignore = true)
    @Mapping(target = "ownerUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateWorkspaceRequest request, @MappingTarget Workspace entity);
}
