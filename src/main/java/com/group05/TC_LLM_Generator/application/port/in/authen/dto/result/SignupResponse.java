package com.group05.TC_LLM_Generator.application.port.in.authen.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {
    private String message;
    private int expiresInSeconds;
    private int cooldownSeconds;
}
