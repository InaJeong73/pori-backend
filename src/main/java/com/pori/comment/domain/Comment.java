package com.pori.comment.domain;

import com.pori.portfolio.domain.Portfolio;
import com.pori.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    private int reportCount = 0;

    private boolean hidden = false;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @Builder
    public Comment(Portfolio portfolio, User author, CommentType type, String body) {
        this.portfolio = portfolio;
        this.author = author;
        this.type = type;
        this.body = body;
    }

    public void incrementReport() {
        this.reportCount++;
        if (this.reportCount >= 3) {
            this.hidden = true;
        }
    }
}
