package com.pori.chat.dto;

import com.pori.chat.domain.Conversation;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(
        UUID id,
        String peerHandle,
        int unreadCount,
        Instant lastMessageAt
) {
    public static ConversationResponse from(Conversation c, UUID myId, String peerHandle) {
        return new ConversationResponse(c.getId(), peerHandle, c.getUnreadFor(myId), c.getLastMessageAt());
    }
}
