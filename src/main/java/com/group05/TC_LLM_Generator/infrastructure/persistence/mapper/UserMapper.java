package com.group05.TC_LLM_Generator.infrastructure.persistence.mapper;

import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "name", source = "fullName")
    @Mapping(target = "password", source = "passwordHash")
    @Mapping(target = "provider", source = "authProvider")
    User toDomain(UserEntity entity);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "fullName", source = "name")
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "authProvider", source = "provider")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastActiveWorkspaceId", ignore = true)
    UserEntity toEntity(User domain);
}
