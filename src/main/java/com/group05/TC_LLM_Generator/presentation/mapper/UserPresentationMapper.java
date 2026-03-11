package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateUserRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateUserRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for User presentation layer
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserPresentationMapper {

    /**
     * Map UserEntity to UserResponse
     */
    @Mapping(target = "role", expression = "java(entity.getRole() != null ? entity.getRole().name() : null)")
    @Mapping(target = "gender", expression = "java(entity.getGender() != null ? entity.getGender().name() : null)")
    UserResponse toResponse(UserEntity entity);

    /**
     * Map list of UserEntity to list of UserResponse
     */
    List<UserResponse> toResponseList(List<UserEntity> entities);

    /**
     * Map CreateUserRequest to UserEntity
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "passwordHash", source = "password")
    UserEntity toEntity(CreateUserRequest request);

    /**
     * Update UserEntity from UpdateUserRequest
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    void updateEntity(UpdateUserRequest request, @MappingTarget UserEntity entity);
}
