package com.pori.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "메시지 전송 요청")
public record MessageCreateRequest(
        @Schema(description = "메시지 본문", example = "포트폴리오 잘 봤습니다. 결제 MSA 전환 경험에 대해 더 이야기해보고 싶어요.")
        @NotBlank String body,

        @Schema(description = "첨부할 포트폴리오 UUID. 첨부가 없으면 null", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID attachedPortfolioId
) {}
