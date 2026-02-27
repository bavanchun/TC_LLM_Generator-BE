package com.group05.TC_LLM_Generator.infrastructure.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String signerKey;

    @Value("${jwt.expiration-accessToken}")
    private long accessTokenDuration; // in milliseconds

    @Value("${jwt.expiration-refreshToken}")
    private long refreshTokenDuration; // in milliseconds

    public String generateAccessToken(Map<String, String> data) {
        return generateToken(data, accessTokenDuration);
    }

    public String generateRefreshToken(Map<String, String> data) {
        return generateToken(data, refreshTokenDuration);
    }

    private String generateToken(Map<String, String> data, long duration) {
        try {
            // Create HMAC signer
            JWSSigner signer = new MACSigner(signerKey.getBytes());

            String userId = data.get("id");
            String email = data.get("email");
            String displayName = data.get("name");
            if (displayName == null || displayName.isEmpty()) {
                displayName = email.split("@")[0];
            }
            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .claim("email", email)
                    .claim("name", displayName)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + duration))
                    .build();

            // Create JWS object with signature algorithm and payload
            JWSObject jwsObject = new JWSObject(
                    new JWSHeader(JWSAlgorithm.HS512),
                    new Payload(claimsSet.toJSONObject()));

            // Apply the HMAC
            jwsObject.sign(signer);

            // Serialize to compact form
            return jwsObject.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }
}
