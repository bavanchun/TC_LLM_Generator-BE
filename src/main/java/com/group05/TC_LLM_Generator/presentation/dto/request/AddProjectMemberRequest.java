package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AddProjectMemberRequest {

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Role is required")
    @Size(max = 50, message = "Role must not exceed 50 characters")
    private String role;
}
