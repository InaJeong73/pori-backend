package com.pori.chat.dto;

import com.pori.chat.domain.Conversation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "채팅방 요약 응답")
public record ConversationResponse(
        @Schema(description = "채팅방 UUID")
        UUID id,

        @Schema(description = "대화 상대 핸들", example = "bob_backend")
        String peerHandle,

        @Schema(description = "내 기준 안 읽은 메시지 수", example = "2")
        int unreadCount,

        @Schema(description = "마지막 메시지 시각 (UTC ISO-8601)")
        Instant lastMessageAt
) {
    public static ConversationResponse from(Conversation c, UUID myId, String peerHandle) {
        return new ConversationResponse(c.getId(), peerHandle, c.getUnreadFor(myId), c.getLastMessageAt());
    }
}
