package com.pori.chat.dto;

import com.pori.chat.domain.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID senderId,
        String body,
        UUID attachedPortfolioId,
        Instant createdAt
) {
    public static MessageResponse from(Message m) {
        return new MessageResponse(m.getId(), m.getSenderId(), m.getBody(),
                m.getAttachedPortfolioId(), m.getCreatedAt());
    }
}
