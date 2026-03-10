package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.domain.repository.InvalidatedTokenRepo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.InvalidatedTokenEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class InvalidatedTokenRepoAdapter implements InvalidatedTokenRepo {

    private final InvalidatedTokenRepository jpaRepository;

    @Override
    public void save(String tokenId, Instant expiryTime) {
        InvalidatedTokenEntity entity = InvalidatedTokenEntity.builder()
                .tokenId(tokenId)
                .expiryTime(expiryTime)
                .build();
        jpaRepository.save(entity);
    }

    @Override
    public boolean existsById(String tokenId) {
        return jpaRepository.existsById(tokenId);
    }
}
