package com.group05.TC_LLM_Generator.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "invalidated_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvalidatedTokenEntity {

    @Id
    @Column(name = "token_id", nullable = false)
    private String tokenId;

    @Column(name = "expiry_time", nullable = false)
    private Instant expiryTime;
}
