package com.group05.TC_LLM_Generator.infrastructure.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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

    public String extractJti(String token) {
        try {
            return com.nimbusds.jwt.SignedJWT.parse(token).getJWTClaimsSet().getJWTID();
        } catch (ParseException e) {
            throw new RuntimeException("Failed to extract jti from token", e);
        }
    }

    public JWTClaimsSet extractClaims(String token) {
        try {
            com.nimbusds.jwt.SignedJWT signedJWT = com.nimbusds.jwt.SignedJWT.parse(token);
            com.nimbusds.jose.crypto.MACVerifier verifier = new com.nimbusds.jose.crypto.MACVerifier(signerKey.getBytes());
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Invalid token signature");
            }
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            if (claims.getExpirationTime() != null && claims.getExpirationTime().before(new Date())) {
                throw new RuntimeException("Token has expired");
            }
            return claims;
        } catch (JOSEException | ParseException e) {
            throw new RuntimeException("Failed to verify token", e);
        }
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
                    .jwtID(UUID.randomUUID().toString())
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
