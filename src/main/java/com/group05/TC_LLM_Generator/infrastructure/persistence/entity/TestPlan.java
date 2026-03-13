package com.group05.TC_LLM_Generator.infrastructure.persistence.entity;

import com.group05.TC_LLM_Generator.domain.model.enums.TestPlanStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for test_plans table
 */
@Entity
@Table(name = "test_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "test_plan_id", nullable = false)
    private UUID testPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity createdByUser;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TestPlanStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "test_plan_stories",
            joinColumns = @JoinColumn(name = "test_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "user_story_id")
    )
    @Builder.Default
    private List<UserStory> userStories = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

}
