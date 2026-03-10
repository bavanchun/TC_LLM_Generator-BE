package com.group05.TC_LLM_Generator.domain.repository;

public interface InvalidatedTokenRepo {
    void save(String tokenId, java.time.Instant expiryTime);

    boolean existsById(String tokenId);
}
