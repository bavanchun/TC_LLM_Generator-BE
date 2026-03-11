package com.group05.TC_LLM_Generator.application.port.in.authen;

import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.SignupRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.SignupResponse;

public interface SignupUseCase {
    SignupResponse execute(SignupRequest request);
}
