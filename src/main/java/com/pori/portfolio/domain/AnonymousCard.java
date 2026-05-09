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
@Table(name = "anonymous_cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AnonymousCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", unique = true, nullable = false)
    private Portfolio portfolio;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String roleSummary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metrics")
    private List<String> metrics = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tech_stack")
    private List<String> techStack = new ArrayList<>();

    private String jobCategory;
    private String yearLevel;
    private String schoolGroupLabel;
    private int totalScore;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @Builder
    public AnonymousCard(Portfolio portfolio, String summary, String roleSummary,
                          List<String> metrics, List<String> techStack,
                          String jobCategory, String yearLevel, String schoolGroupLabel,
                          int totalScore) {
        this.portfolio = portfolio;
        this.summary = summary;
        this.roleSummary = roleSummary;
        this.metrics = metrics != null ? metrics : new ArrayList<>();
        this.techStack = techStack != null ? techStack : new ArrayList<>();
        this.jobCategory = jobCategory;
        this.yearLevel = yearLevel;
        this.schoolGroupLabel = schoolGroupLabel;
        this.totalScore = totalScore;
    }
}
