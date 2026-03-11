package com.group05.TC_LLM_Generator.application.service.authen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group05.TC_LLM_Generator.application.port.in.authen.VerifySignupUseCase;
import com.group05.TC_LLM_Generator.application.port.out.authen.RegistrationCachePort;
import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.domain.repository.UserRepo;
import com.group05.TC_LLM_Generator.infrastructure.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifySignupService implements VerifySignupUseCase {

    private final RegistrationCachePort registrationCache;
    private final UserRepo userRepo;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public String execute(String token, String email) {
        String hashedEmail = HashUtil.sha256(email);

        // 1. Lookup OTP
        Optional<String> storedOtp = registrationCache.getOtp(hashedEmail);
        if (storedOtp.isEmpty() || !storedOtp.get().equals(token)) {
            log.warn("Invalid verification attempt for email: {}", email);
            return frontendUrl + "/signup?error=invalid";
        }

        // 2. Get registration info
        Optional<String> infoJson = registrationCache.getRegistrationInfo(hashedEmail);
        if (infoJson.isEmpty()) {
            log.warn("Registration info expired for email: {}", email);
            return frontendUrl + "/signup?error=expired";
        }

        // 3. Parse info and save user to DB
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> info = objectMapper.readValue(infoJson.get(), Map.class);

            User newUser = User.builder()
                    .email(info.get("email"))
                    .name(info.get("fullName"))
                    .password(info.get("passwordHash"))
                    .provider("LOCAL")
                    .status("ACTIVE")
                    .build();
            userRepo.save(newUser);

            log.info("User account created successfully for email: {}", email);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse registration info for email: {}", email, e);
            return frontendUrl + "/signup?error=invalid";
        }

        // 4. Cleanup Redis
        registrationCache.clearRegistrationData(hashedEmail);

        // 5. Redirect to success
        return frontendUrl + "/signup?verified=true";
    }
}
