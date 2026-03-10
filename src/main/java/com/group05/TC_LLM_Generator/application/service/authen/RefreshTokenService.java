package com.group05.TC_LLM_Generator.application.service.authen;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group05.TC_LLM_Generator.application.port.in.authen.RefreshTokenUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;
import com.group05.TC_LLM_Generator.domain.repository.RefreshTokenRepo;
import com.group05.TC_LLM_Generator.infrastructure.security.JwtTokenProvider;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepo refreshTokenRepo;

    @Override
    @Transactional
    public AuthResponse execute(String refreshToken) {
        JWTClaimsSet claims = jwtTokenProvider.extractClaims(refreshToken);

        String jti = claims.getJWTID();
        if (jti == null || !refreshTokenRepo.existsById(jti)) {
            throw new RuntimeException("Refresh token is invalid or has been revoked");
        }

        // Revoke old refresh token
        refreshTokenRepo.deleteById(jti);

        // Generate new token pair
        String userId = claims.getSubject();
        String email = (String) claims.getClaim("email");
        String name = (String) claims.getClaim("name");

        Map<String, String> data = new HashMap<>();
        data.put("id", userId);
        data.put("email", email);
        data.put("name", name);

        String newAccessToken = jwtTokenProvider.generateAccessToken(data);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(data);

        // Store new refresh token
        String newJti = jwtTokenProvider.extractJti(newRefreshToken);
        refreshTokenRepo.save(newJti, UUID.fromString(userId), claims.getExpirationTime().toInstant());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
