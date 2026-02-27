package com.group05.TC_LLM_Generator.application.service.authen;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.group05.TC_LLM_Generator.application.port.in.authen.LoginUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;
import com.group05.TC_LLM_Generator.application.port.out.authen.VerifyTokenPort;
import com.group05.TC_LLM_Generator.application.port.out.authen.dto.info.GoogleUserInfo;
import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.domain.repository.UserRepo;
import com.group05.TC_LLM_Generator.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UserRepo userRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerifyTokenPort verifyTokenPort;

    @Override
    public AuthResponse execute(String idTokenString) {
        // 1. Verify Google Token via Output Port
        GoogleUserInfo googleUser = verifyTokenPort.execute(idTokenString);

        // 2. Find User or Create
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

        Map<String, String> data = new HashMap<>();
        data.put("id", user.getId().toString());
        data.put("email", user.getEmail());
        data.put("name", user.getName());
        // 3. Generate Tokens
        String accessToken = jwtTokenProvider.generateAccessToken(data);
        String refreshToken = jwtTokenProvider.generateRefreshToken(data);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
