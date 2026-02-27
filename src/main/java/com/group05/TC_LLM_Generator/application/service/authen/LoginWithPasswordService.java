package com.group05.TC_LLM_Generator.application.service.authen;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.group05.TC_LLM_Generator.application.port.in.authen.LoginWithPasswordUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.LoginRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;
import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.infrastructure.security.CustomUserDetails;
import com.group05.TC_LLM_Generator.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginWithPasswordService implements LoginWithPasswordUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse execute(LoginRequest request) {
        // 1. Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // 2. Get User Details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 3. Generate Tokens
        Map<String, String> data = new HashMap<>();
        data.put("id", user.getId().toString());
        data.put("email", user.getEmail());
        data.put("name", user.getName());

        String accessToken = jwtTokenProvider.generateAccessToken(data);
        String refreshToken = jwtTokenProvider.generateRefreshToken(data);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
