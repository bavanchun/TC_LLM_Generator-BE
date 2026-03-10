package com.group05.TC_LLM_Generator.application.service.authen;

import com.group05.TC_LLM_Generator.application.port.in.authen.LogoutUseCase;
import com.group05.TC_LLM_Generator.domain.repository.InvalidatedTokenRepo;
import com.group05.TC_LLM_Generator.domain.repository.RefreshTokenRepo;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    private final InvalidatedTokenRepo invalidatedTokenRepo;
    private final RefreshTokenRepo refreshTokenRepo;

    @Override
    @Transactional
    public void execute(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            String userId = signedJWT.getJWTClaimsSet().getSubject();

            if (jti == null) {
                throw new RuntimeException("Token does not contain a jti claim");
            }

            // Blacklist the access token (skip if already expired)
            if (expiryTime == null || !expiryTime.toInstant().isBefore(Instant.now())) {
                invalidatedTokenRepo.save(jti, expiryTime != null ? expiryTime.toInstant() : Instant.now().plusSeconds(3600));
            }

            // Revoke all refresh tokens for this user
            if (userId != null) {
                refreshTokenRepo.deleteByUserId(UUID.fromString(userId));
            }
        } catch (ParseException e) {
            throw new RuntimeException("Invalid token format", e);
        }
    }
}
