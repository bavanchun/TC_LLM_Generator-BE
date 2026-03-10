package com.group05.TC_LLM_Generator.domain.repository;

import java.time.Instant;
import java.util.UUID;

public interface RefreshTokenRepo {
    void save(String tokenId, UUID userId, Instant expiryTime);

    boolean existsById(String tokenId);

    void deleteById(String tokenId);

    void deleteByUserId(UUID userId);
}
