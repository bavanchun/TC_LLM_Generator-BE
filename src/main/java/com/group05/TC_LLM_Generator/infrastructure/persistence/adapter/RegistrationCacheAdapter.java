package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.authen.RegistrationCachePort;
import com.group05.TC_LLM_Generator.infrastructure.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistrationCacheAdapter implements RegistrationCachePort {

    private final RedisUtil redisUtil;

    private static final String PREFIX_INFO = "newuser_infor:";
    private static final String PREFIX_OTP = "newuser_otp:";
    private static final String PREFIX_FREEZER = "newuser_freezer:";

    @Override
    public void storeRegistrationInfo(String hashedEmail, String infoJson, long ttlSeconds) {
        redisUtil.setString(PREFIX_INFO + hashedEmail, infoJson, ttlSeconds);
    }

    @Override
    public Optional<String> getRegistrationInfo(String hashedEmail) {
        return redisUtil.getString(PREFIX_INFO + hashedEmail);
    }

    @Override
    public void storeOtp(String hashedEmail, String otp, long ttlSeconds) {
        redisUtil.setString(PREFIX_OTP + hashedEmail, otp, ttlSeconds);
    }

    @Override
    public Optional<String> getOtp(String hashedEmail) {
        return redisUtil.getString(PREFIX_OTP + hashedEmail);
    }

    @Override
    public void storeFreezer(String hashedEmail, long ttlSeconds) {
        redisUtil.setString(PREFIX_FREEZER + hashedEmail, "1", ttlSeconds);
    }

    @Override
    public boolean isFrozen(String hashedEmail) {
        return redisUtil.exists(PREFIX_FREEZER + hashedEmail);
    }

    @Override
    public long getFreezerTtl(String hashedEmail) {
        return redisUtil.getTtl(PREFIX_FREEZER + hashedEmail);
    }

    @Override
    public void clearRegistrationData(String hashedEmail) {
        redisUtil.delete(PREFIX_INFO + hashedEmail);
        redisUtil.delete(PREFIX_OTP + hashedEmail);
        redisUtil.delete(PREFIX_FREEZER + hashedEmail);
    }
}
