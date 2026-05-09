package com.pori.chat.dto;

import com.pori.chat.domain.Message;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "메시지 응답")
public record MessageResponse(
        @Schema(description = "메시지 UUID")
        UUID id,

        @Schema(description = "발신자 사용자 UUID")
        UUID senderId,

        @Schema(description = "메시지 본문", example = "포트폴리오 잘 봤습니다.")
        String body,

        @Schema(description = "첨부된 포트폴리오 UUID. 첨부가 없으면 null")
        UUID attachedPortfolioId,

        @Schema(description = "메시지 생성 시각 (UTC ISO-8601)")
        Instant createdAt
) {
    public static MessageResponse from(Message m) {
        return new MessageResponse(m.getId(), m.getSenderId(), m.getBody(),
                m.getAttachedPortfolioId(), m.getCreatedAt());
    }
}
