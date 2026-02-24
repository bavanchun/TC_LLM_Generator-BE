package com.group05.TC_LLM_Generator.application.port.in.authen;

import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;

public interface LoginUseCase {
    AuthResponse execute(String idToken);
}
