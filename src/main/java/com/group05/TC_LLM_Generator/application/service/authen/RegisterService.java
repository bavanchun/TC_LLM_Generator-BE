package com.group05.TC_LLM_Generator.application.service.authen;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group05.TC_LLM_Generator.application.port.in.authen.RegisterUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.RegisterRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;
import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.domain.repository.UserRepo;
import com.group05.TC_LLM_Generator.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase {

    private final UserRepo userRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse execute(RegisterRequest request) {
        // 1. Check if user already exists
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Create new User
        User newUser = User.builder()
                .email(request.getEmail())
                .name(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .provider("LOCAL")
                .status("ACTIVE")
                .build();
        User savedUser = userRepo.save(newUser);

        // 3. Generate Tokens
        Map<String, String> data = new HashMap<>();
        data.put("id", savedUser.getId().toString());
        data.put("email", savedUser.getEmail());
        data.put("name", savedUser.getName());

        String accessToken = jwtTokenProvider.generateAccessToken(data);
        String refreshToken = jwtTokenProvider.generateRefreshToken(data);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
