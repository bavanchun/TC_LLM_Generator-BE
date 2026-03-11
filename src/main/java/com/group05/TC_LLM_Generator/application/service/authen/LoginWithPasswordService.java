package com.group05.TC_LLM_Generator.application.service.authen;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group05.TC_LLM_Generator.application.port.in.authen.LoginWithPasswordUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.LoginRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;
import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceService;
import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.domain.repository.RefreshTokenRepo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.security.CustomUserDetails;
import com.group05.TC_LLM_Generator.infrastructure.security.JwtTokenProvider;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginWithPasswordService implements LoginWithPasswordUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserService userService;
    private final WorkspaceService workspaceService;

    @Override
    @Transactional
    public AuthResponse execute(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        ensureDefaultWorkspace(user.getId(), user.getName());

        Map<String, String> data = new HashMap<>();
        data.put("id", user.getId().toString());
        data.put("email", user.getEmail());
        data.put("name", user.getName());
        data.put("roles", user.getRole().name());

        String accessToken = jwtTokenProvider.generateAccessToken(data);
        String refreshToken = jwtTokenProvider.generateRefreshToken(data);

        JWTClaimsSet refreshClaims = jwtTokenProvider.extractClaims(refreshToken);
        refreshTokenRepo.save(
                refreshClaims.getJWTID(),
                user.getId(),
                refreshClaims.getExpirationTime().toInstant()
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void ensureDefaultWorkspace(java.util.UUID userId, String userName) {
        if (!workspaceService.hasAnyWorkspace(userId)) {
            UserEntity owner = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Workspace workspace = Workspace.builder()
                    .ownerUser(owner)
                    .name(userName + "'s Workspace")
                    .description("Default workspace")
                    .build();
            workspaceService.createWorkspace(workspace);
        }
    }
}
