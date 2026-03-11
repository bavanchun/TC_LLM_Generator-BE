package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for User entity with HATEOAS support
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse extends RepresentationModel<UserResponse> {

    private UUID userId;
    private String email;
    private String fullName;
    private String status;
    private String role;
    private String authProvider;
    private String gender;
    private Instant createdAt;
    private Instant updatedAt;
}
