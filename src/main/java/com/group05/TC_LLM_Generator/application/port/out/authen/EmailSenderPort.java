package com.group05.TC_LLM_Generator.application.port.out.authen;

public interface EmailSenderPort {
    void sendVerificationEmail(String toEmail, String fullName, String verificationUrl);
}
