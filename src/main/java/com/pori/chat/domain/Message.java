package com.pori.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages",
       indexes = @Index(columnList = "conversation_id, created_at DESC"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    private UUID attachedPortfolioId;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @Builder
    public Message(UUID conversationId, UUID senderId, String body, UUID attachedPortfolioId) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.body = body;
        this.attachedPortfolioId = attachedPortfolioId;
    }
}
