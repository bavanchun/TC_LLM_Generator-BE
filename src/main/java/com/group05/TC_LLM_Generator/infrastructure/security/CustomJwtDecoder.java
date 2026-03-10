package com.group05.TC_LLM_Generator.infrastructure.security;

import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import com.group05.TC_LLM_Generator.domain.repository.InvalidatedTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.secret}")
    private String signerKey;

    private final InvalidatedTokenRepo invalidatedTokenRepo;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        if (token.startsWith("ya29.")) {
            throw new JwtException("Google access token - skip local JWT decode");
        }

        try {
            if (Objects.isNull(nimbusJwtDecoder)) {
                SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");
                nimbusJwtDecoder = NimbusJwtDecoder
                        .withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build();
            }

            Jwt jwt = nimbusJwtDecoder.decode(token);

            String jti = jwt.getId();
            if (jti != null && invalidatedTokenRepo.existsById(jti)) {
                throw new JwtException("Token has been revoked");
            }

            return jwt;

        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtException("JWT decode failed: " + e.getMessage());
        }
    }
}
