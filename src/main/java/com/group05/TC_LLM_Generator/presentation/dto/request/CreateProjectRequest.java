package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

    @NotNull(message = "Workspace ID is required")
    private UUID workspaceId;

    @NotBlank(message = "Project key is required")
    @Size(max = 20, message = "Project key must not exceed 20 characters")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "Project key must start with uppercase letter and contain only uppercase letters, numbers, and underscores")
    private String projectKey;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Size(max = 100, message = "Jira site ID must not exceed 100 characters")
    private String jiraSiteId;

    @Size(max = 50, message = "Jira project key must not exceed 50 characters")
    private String jiraProjectKey;
}
