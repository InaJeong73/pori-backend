package com.pori.portfolio.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "portfolios")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobCategory jobCategory;

    @Column(nullable = false)
    private String portfolioUrl;

    @Column(length = 1000)
    private String skills;           // comma-separated

    @Column(nullable = false, length = 2000)
    private String mainProject;

    @Column(nullable = false, length = 2000)
    private String role;

    @Column(length = 2000)
    private String problemSolved;

    @Column(length = 2000)
    private String techReason;

    @Column(length = 2000)
    private String result;

    private String githubUrl;
    private String deployUrl;

    @Column(nullable = false, length = 2000)
    private String summary;

    // Evaluation results
    private int poriScore;

    @Enumerated(EnumType.STRING)
    private PoriGrade grade;

    private boolean isPublic;
    private boolean isTopReference;
    private boolean isHighTrust;

    @Embedded
    private CategoryScores categoryScores;

    @Column(length = 3000)
    private String strengths;        // JSON array as string

    @Column(length = 3000)
    private String improvements;     // JSON array as string

    @Column(length = 2000)
    private String recommendation;

    @CreatedDate
    private LocalDateTime createdAt;

    public List<String> getSkillList() {
        if (skills == null || skills.isBlank()) return List.of();
        return Arrays.stream(skills.split(",")).map(String::trim).toList();
    }

    public List<String> getStrengthList() {
        return parseJsonArray(strengths);
    }

    public List<String> getImprovementList() {
        return parseJsonArray(improvements);
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.isBlank()) return List.of();
        String inner = json.strip();
        if (inner.startsWith("[")) inner = inner.substring(1);
        if (inner.endsWith("]")) inner = inner.substring(0, inner.length() - 1);
        return Arrays.stream(inner.split("\",\""))
                .map(s -> s.replaceAll("^\"|\"$", "").trim())
                .filter(s -> !s.isBlank())
                .toList();
    }
}
