package com.group05.TC_LLM_Generator.application.port.out.authen;

import com.group05.TC_LLM_Generator.application.port.out.authen.dto.info.GoogleUserInfo;

public interface VerifyTokenPort {
    GoogleUserInfo execute(String idTokenString);
}
