package com.group05.TC_LLM_Generator.application.service.authen;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group05.TC_LLM_Generator.application.port.in.authen.RegisterUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.RegisterRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;
import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceService;
import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.domain.model.enums.Role;
import com.group05.TC_LLM_Generator.domain.repository.RefreshTokenRepo;
import com.group05.TC_LLM_Generator.domain.repository.UserRepo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.security.JwtTokenProvider;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase {

    private final UserRepo userRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserService userService;
    private final WorkspaceService workspaceService;

    @Override
    @Transactional
    public AuthResponse execute(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .name(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .provider("LOCAL")
                .status("ACTIVE")
                .role(Role.USER)
                .build();
        User savedUser = userRepo.save(newUser);

        createDefaultWorkspace(savedUser.getId(), savedUser.getName());

        Map<String, String> data = new HashMap<>();
        data.put("id", savedUser.getId().toString());
        data.put("email", savedUser.getEmail());
        data.put("name", savedUser.getName());
        data.put("roles", savedUser.getRole().name());

        String accessToken = jwtTokenProvider.generateAccessToken(data);
        String refreshToken = jwtTokenProvider.generateRefreshToken(data);

        JWTClaimsSet refreshClaims = jwtTokenProvider.extractClaims(refreshToken);
        refreshTokenRepo.save(
                refreshClaims.getJWTID(),
                savedUser.getId(),
                refreshClaims.getExpirationTime().toInstant()
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void createDefaultWorkspace(java.util.UUID userId, String userName) {
        UserEntity owner = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        Workspace workspace = Workspace.builder()
                .ownerUser(owner)
                .name(userName + "'s Workspace")
                .description("Default workspace")
                .build();
        workspaceService.createWorkspace(workspace);
    }
}
