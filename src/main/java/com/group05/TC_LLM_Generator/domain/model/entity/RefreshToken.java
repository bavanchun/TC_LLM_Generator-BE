package com.group05.TC_LLM_Generator.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private String tokenId;
    private UUID userId;
    private Instant expiryTime;
    private Instant createdAt;
}
