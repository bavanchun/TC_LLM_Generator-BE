package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSuiteResponse extends RepresentationModel<TestSuiteResponse> {

    private UUID testSuiteId;
    private UUID projectId;
    private String projectName;
    private String name;
    private String description;
    private Instant createdAt;
}
