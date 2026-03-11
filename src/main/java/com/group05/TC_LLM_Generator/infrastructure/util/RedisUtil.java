package com.group05.TC_LLM_Generator.infrastructure.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    public RedisUtil(
            @Qualifier("redisStringTemplate") RedisTemplate<String, String> stringRedisTemplate,
            @Qualifier("redisObjectTemplate") RedisTemplate<String, Object> objectRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectRedisTemplate = objectRedisTemplate;
    }

    // --- String operations ---
    public void setString(String key, String value, long ttlSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    public Optional<String> getString(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    // --- Object operations (auto JSON serialization) ---
    public void setObject(String key, Object value, long ttlSeconds) {
        objectRedisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getObject(String key, Class<T> clazz) {
        Object value = objectRedisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        if (clazz.isInstance(value)) return Optional.of((T) value);
        return Optional.empty();
    }

    // --- Common operations ---
    public boolean delete(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public long getTtl(String key) {
        Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : -2;
    }
}
