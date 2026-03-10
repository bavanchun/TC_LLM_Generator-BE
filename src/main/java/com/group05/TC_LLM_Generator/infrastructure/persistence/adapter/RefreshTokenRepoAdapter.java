package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.domain.repository.RefreshTokenRepo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.RefreshTokenEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepoAdapter implements RefreshTokenRepo {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public void save(String tokenId, UUID userId, Instant expiryTime) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .tokenId(tokenId)
                .userId(userId)
                .expiryTime(expiryTime)
                .build();
        jpaRepository.save(entity);
    }

    @Override
    public boolean existsById(String tokenId) {
        return jpaRepository.existsById(tokenId);
    }

    @Override
    public void deleteById(String tokenId) {
        jpaRepository.deleteById(tokenId);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }
}
