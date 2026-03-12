package com.group05.TC_LLM_Generator.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for acceptance_criteria table
 */
@Entity
@Table(name = "acceptance_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptanceCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "acceptance_criteria_id", nullable = false)
    private UUID acceptanceCriteriaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_story_id", referencedColumnName = "user_story_id", nullable = false)
    private UserStory userStory;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_acceptance_criteria_id", referencedColumnName = "acceptance_criteria_id")
    private AcceptanceCriteria parentAcceptanceCriteria;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "completed", nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

}
