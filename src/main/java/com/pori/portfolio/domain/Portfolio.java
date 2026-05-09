package com.pori.portfolio.domain;

import com.pori.user.domain.User;
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
@Table(name = "portfolios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int version = 1;

    private UUID parentPortfolioId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String intro;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tech_stack")
    private List<String> techStack = new ArrayList<>();

    @Column(nullable = false)
    private short privacyLevel = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PortfolioStatus status = PortfolioStatus.PENDING;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    private Instant publishedAt;

    @Builder
    public Portfolio(User user, String title, String intro, List<String> techStack, short privacyLevel) {
        this.user = user;
        this.title = title;
        this.intro = intro;
        this.techStack = techStack != null ? techStack : new ArrayList<>();
        this.privacyLevel = privacyLevel;
    }

    public void startEvaluating() {
        this.status = PortfolioStatus.EVALUATING;
    }

    public void publish() {
        this.status = PortfolioStatus.PUBLISHED;
        this.publishedAt = Instant.now();
    }

    public void fail() {
        this.status = PortfolioStatus.FAILED;
    }

    public void archive() {
        this.status = PortfolioStatus.ARCHIVED;
    }

    public void update(String title, String intro, List<String> techStack, short privacyLevel) {
        if (title != null) this.title = title;
        if (intro != null) this.intro = intro;
        if (techStack != null) this.techStack = techStack;
        this.privacyLevel = privacyLevel;
    }

    public void setVersion(int version, UUID parentId) {
        this.version = version;
        this.parentPortfolioId = parentId;
    }
}
