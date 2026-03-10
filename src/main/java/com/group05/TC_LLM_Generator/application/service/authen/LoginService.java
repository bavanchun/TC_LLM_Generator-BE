package com.group05.TC_LLM_Generator.application.service.authen;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group05.TC_LLM_Generator.application.port.in.authen.LoginUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;
import com.group05.TC_LLM_Generator.application.port.out.authen.VerifyTokenPort;
import com.group05.TC_LLM_Generator.application.port.out.authen.dto.info.GoogleUserInfo;
import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceService;
import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.domain.repository.RefreshTokenRepo;
import com.group05.TC_LLM_Generator.domain.repository.UserRepo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.security.JwtTokenProvider;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UserRepo userRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerifyTokenPort verifyTokenPort;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserService userService;
    private final WorkspaceService workspaceService;

    @Override
    @Transactional
    public AuthResponse execute(String idTokenString) {
        GoogleUserInfo googleUser = verifyTokenPort.execute(idTokenString);

        User user = userRepo.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(googleUser.getEmail())
                            .name(googleUser.getName())
                            .provider("GOOGLE")
                            .status("ACTIVE")
                            .build();
                    return userRepo.save(newUser);
                });

        ensureDefaultWorkspace(user.getId(), user.getName());

        Map<String, String> data = new HashMap<>();
        data.put("id", user.getId().toString());
        data.put("email", user.getEmail());
        data.put("name", user.getName());

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
