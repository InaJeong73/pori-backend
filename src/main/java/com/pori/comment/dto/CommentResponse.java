package com.pori.comment.dto;

import com.pori.comment.domain.Comment;
import com.pori.comment.domain.CommentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "댓글 응답")
public record CommentResponse(
        @Schema(description = "댓글 UUID")
        UUID id,

        @Schema(description = "댓글 작성자 핸들", example = "alice_fe")
        String authorHandle,

        @Schema(description = "작성자의 대표 포트폴리오 AI 점수", example = "82")
        int authorScore,

        @Schema(description = "작성자가 엘리트 배지 대상인지 여부 (대표 점수 90점 이상)", example = "false")
        boolean isElite,

        @Schema(description = "댓글 유형. PRAISE / SUGGEST / QUESTION", example = "SUGGEST")
        CommentType type,

        @Schema(description = "댓글 본문", example = "문제 해결 과정이 더 구체적으로 보이면 좋겠습니다.")
        String body,

        @Schema(description = "댓글 작성 시각 (UTC ISO-8601)")
        Instant createdAt
) {
    public static CommentResponse from(Comment c, int authorScore) {
        return new CommentResponse(c.getId(), c.getAuthor().getHandle(),
                authorScore, authorScore >= 90,
                c.getType(), c.getBody(), c.getCreatedAt());
    }
}
