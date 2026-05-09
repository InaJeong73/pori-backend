package com.pori.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_a", "user_b"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_a", nullable = false)
    private UUID userA;

    @Column(name = "user_b", nullable = false)
    private UUID userB;

    private Instant lastMessageAt;
    private int unreadA = 0;
    private int unreadB = 0;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    public static Conversation between(UUID userId1, UUID userId2) {
        Conversation c = new Conversation();
        // Always store lower UUID as userA for uniqueness
        if (userId1.compareTo(userId2) <= 0) {
            c.userA = userId1;
            c.userB = userId2;
        } else {
            c.userA = userId2;
            c.userB = userId1;
        }
        return c;
    }

    public void onNewMessage(UUID senderId) {
        this.lastMessageAt = Instant.now();
        if (senderId.equals(userA)) {
            this.unreadB++;
        } else {
            this.unreadA++;
        }
    }

    public void markRead(UUID readerId) {
        if (readerId.equals(userA)) {
            this.unreadA = 0;
        } else {
            this.unreadB = 0;
        }
    }

    public UUID getOtherUserId(UUID myId) {
        return myId.equals(userA) ? userB : userA;
    }

    public int getUnreadFor(UUID userId) {
        return userId.equals(userA) ? unreadA : unreadB;
    }
}
