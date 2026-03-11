package com.group05.TC_LLM_Generator.application.service.authen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group05.TC_LLM_Generator.application.port.in.authen.SignupUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.SignupRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.SignupResponse;
import com.group05.TC_LLM_Generator.application.port.out.authen.EmailSenderPort;
import com.group05.TC_LLM_Generator.application.port.out.authen.RegistrationCachePort;
import com.group05.TC_LLM_Generator.domain.repository.UserRepo;
import com.group05.TC_LLM_Generator.infrastructure.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignupService implements SignupUseCase {

    private final UserRepo userRepo;
    private final RegistrationCachePort registrationCache;
    private final EmailSenderPort emailSender;
    private final PasswordEncoder passwordEncoder;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.backend-url}")
    private String backendUrl;

    private static final int OTP_LENGTH = 20;
    private static final long INFO_TTL_SECONDS = 300; // 5 minutes
    private static final long FREEZER_TTL_SECONDS = 60; // 1 minute

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public SignupResponse execute(SignupRequest request) {
        String hashedEmail = HashUtil.sha256(request.getEmail());

        // 1. Check freezer
        if (registrationCache.isFrozen(hashedEmail)) {
            long remaining = registrationCache.getFreezerTtl(hashedEmail);
            throw new RuntimeException("Please wait " + remaining + " seconds before requesting again");
        }

        // 2. Check if email already exists in DB
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // 3. Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // 4. Build user info JSON (with hashed password)
        String infoJson = buildInfoJson(request);

        // 5. Store registration info in Redis
        registrationCache.storeRegistrationInfo(hashedEmail, infoJson, INFO_TTL_SECONDS);

        // 6. Generate and store OTP
        String otp = generateOtp();
        registrationCache.storeOtp(hashedEmail, otp, INFO_TTL_SECONDS);

        // 7. Store freezer (rate-limit)
        registrationCache.storeFreezer(hashedEmail, FREEZER_TTL_SECONDS);

        // 8. Build verification URL and send email
        String verificationUrl = backendUrl + "/api/v1/auth/verify?token=" + otp
                + "&email=" + request.getEmail();
        emailSender.sendVerificationEmail(request.getEmail(), request.getFullName(), verificationUrl);

        // 9. Return response
        return SignupResponse.builder()
                .message("Verification email sent to " + request.getEmail())
                .expiresInSeconds((int) INFO_TTL_SECONDS)
                .cooldownSeconds((int) FREEZER_TTL_SECONDS)
                .build();
    }

    private String buildInfoJson(SignupRequest request) {
        try {
            Map<String, String> info = new HashMap<>();
            info.put("email", request.getEmail());
            info.put("fullName", request.getFullName());
            info.put("passwordHash", passwordEncoder.encode(request.getPassword()));
            return objectMapper.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize registration info", e);
        }
    }

    private String generateOtp() {
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
