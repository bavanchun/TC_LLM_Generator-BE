package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.port.in.authen.LoginUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;

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

    @PostMapping("/login-google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        AuthResponse response = loginUseCase.execute(idToken);
        return ResponseEntity.ok(response);
    }

}
