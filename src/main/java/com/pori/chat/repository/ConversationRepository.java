package com.pori.chat.repository;

import com.pori.chat.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("SELECT c FROM Conversation c WHERE c.userA = :userId OR c.userB = :userId ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<Conversation> findByUserId(@Param("userId") UUID userId);

    @Query("""
        SELECT c FROM Conversation c
        WHERE (c.userA = :a AND c.userB = :b) OR (c.userA = :b AND c.userB = :a)
        """)
    Optional<Conversation> findBetween(@Param("a") UUID a, @Param("b") UUID b);
}
