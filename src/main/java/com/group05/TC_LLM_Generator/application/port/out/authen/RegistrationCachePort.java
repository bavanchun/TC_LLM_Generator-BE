package com.group05.TC_LLM_Generator.application.port.out.authen;

import java.util.Optional;

public interface RegistrationCachePort {
    void storeRegistrationInfo(String hashedEmail, String infoJson, long ttlSeconds);
    Optional<String> getRegistrationInfo(String hashedEmail);
    void storeOtp(String hashedEmail, String otp, long ttlSeconds);
    Optional<String> getOtp(String hashedEmail);
    void storeFreezer(String hashedEmail, long ttlSeconds);
    boolean isFrozen(String hashedEmail);
    long getFreezerTtl(String hashedEmail);
    void clearRegistrationData(String hashedEmail);
}
