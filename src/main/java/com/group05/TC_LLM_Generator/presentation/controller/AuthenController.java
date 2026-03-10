package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.port.in.authen.LoginUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.LoginWithPasswordUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.LogoutUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.RefreshTokenUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.RegisterUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.LoginRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.RegisterRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenController {

    private final LoginUseCase loginUseCase;
    private final LoginWithPasswordUseCase loginWithPasswordUseCase;
    private final RegisterUseCase registerUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/login-google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        AuthResponse response = loginUseCase.execute(idToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = loginWithPasswordUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = registerUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        AuthResponse response = refreshTokenUseCase.execute(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutUseCase.execute(token);
        }
        return ResponseEntity.ok().build();
    }
}
