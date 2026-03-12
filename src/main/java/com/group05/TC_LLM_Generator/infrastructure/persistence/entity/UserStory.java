package com.group05.TC_LLM_Generator.infrastructure.persistence.entity;

import com.group05.TC_LLM_Generator.domain.model.enums.StoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for user_stories table
 */
@Entity
@Table(name = "user_stories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_story_id", nullable = false)
    private UUID userStoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;

    @Column(name = "jira_issue_key", length = 50)
    private String jiraIssueKey;

    @Column(name = "jira_issue_id", length = 50)
    private String jiraIssueId;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "as_a", length = 500)
    private String asA;

    @Column(name = "i_want_to", columnDefinition = "TEXT")
    private String iWantTo;

    @Column(name = "so_that", columnDefinition = "TEXT")
    private String soThat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private StoryStatus status;

    @OneToMany(mappedBy = "userStory", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNo ASC")
    @Builder.Default
    private List<AcceptanceCriteria> acceptanceCriteria = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

}
