package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestSuiteRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestSuiteRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestSuiteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TestSuitePresentationMapper {

    @Mapping(target = "projectId", source = "project.projectId")
    @Mapping(target = "projectName", source = "project.name")
    TestSuiteResponse toResponse(TestSuite entity);

    List<TestSuiteResponse> toResponseList(List<TestSuite> entities);

    @Mapping(target = "testSuiteId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "testSuiteItems", ignore = true)
    @Mapping(target = "planSuites", ignore = true)
    TestSuite toEntity(CreateTestSuiteRequest request);

    @Mapping(target = "testSuiteId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "testSuiteItems", ignore = true)
    @Mapping(target = "planSuites", ignore = true)
    void updateEntity(UpdateTestSuiteRequest request, @MappingTarget TestSuite entity);
}
