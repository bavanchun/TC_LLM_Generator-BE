package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateAcceptanceCriteriaRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateAcceptanceCriteriaRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.AcceptanceCriteriaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for AcceptanceCriteria presentation layer
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AcceptanceCriteriaPresentationMapper {

    /**
     * Map AcceptanceCriteria to AcceptanceCriteriaResponse
     */
    @Mapping(target = "userStoryId", source = "userStory.userStoryId")
    AcceptanceCriteriaResponse toResponse(AcceptanceCriteria entity);

    /**
     * Map list of AcceptanceCriteria to list of AcceptanceCriteriaResponse
     */
    List<AcceptanceCriteriaResponse> toResponseList(List<AcceptanceCriteria> entities);

    /**
     * Map CreateAcceptanceCriteriaRequest to AcceptanceCriteria
     */
    @Mapping(target = "acceptanceCriteriaId", ignore = true)
    @Mapping(target = "userStory", ignore = true)
    @Mapping(target = "parentAcceptanceCriteria", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AcceptanceCriteria toEntity(CreateAcceptanceCriteriaRequest request);

    /**
     * Map list of CreateAcceptanceCriteriaRequest to list of AcceptanceCriteria
     */
    List<AcceptanceCriteria> toEntityList(List<CreateAcceptanceCriteriaRequest> requests);

    /**
     * Update AcceptanceCriteria from UpdateAcceptanceCriteriaRequest
     */
    @Mapping(target = "acceptanceCriteriaId", ignore = true)
    @Mapping(target = "userStory", ignore = true)
    @Mapping(target = "parentAcceptanceCriteria", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(UpdateAcceptanceCriteriaRequest request, @MappingTarget AcceptanceCriteria entity);
}
