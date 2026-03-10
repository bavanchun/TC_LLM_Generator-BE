package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectMemberRequest {

    @Size(max = 50, message = "Role must not exceed 50 characters")
    private String role;
}
