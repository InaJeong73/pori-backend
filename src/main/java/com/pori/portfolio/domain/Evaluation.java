package com.pori.portfolio.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "evaluations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", unique = true, nullable = false)
    private Portfolio portfolio;

    private int totalScore;

    @Column(columnDefinition = "TEXT")
    private String scoresJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "weakness_categories")
    private List<String> weaknessCategories = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String improvementsJson;

    @Column(columnDefinition = "TEXT")
    private String firstImpression;

    @Column(nullable = false)
    private String evaluatorType = "ai";

    private String aiModelVersion = "mock-v1";

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @Builder
    public Evaluation(Portfolio portfolio, int totalScore, String scoresJson,
                       List<String> weaknessCategories, String improvementsJson,
                       String firstImpression) {
        this.portfolio = portfolio;
        this.totalScore = totalScore;
        this.scoresJson = scoresJson;
        this.weaknessCategories = weaknessCategories != null ? weaknessCategories : new ArrayList<>();
        this.improvementsJson = improvementsJson;
        this.firstImpression = firstImpression;
    }
}
