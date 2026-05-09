package com.pori.feed.dto;

import com.pori.portfolio.domain.AnonymousCard;
import com.pori.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Schema(description = "익명 포트폴리오 피드 카드 응답")
public record FeedItemResponse(
        @Schema(description = "익명 카드 UUID")
        UUID cardId,

        @Schema(description = "작성자 핸들", example = "alice_fe")
        String handle,

        @Schema(description = "아바타 정보. 이미지 URL 또는 그라디언트 시드가 내려옵니다.")
        Map<String, Object> avatar,

        @Schema(description = "포트폴리오 핵심 요약", example = "결제 시스템 MSA 전환으로 TPS를 4배 개선")
        String summary,

        @Schema(description = "역할 요약", example = "백엔드 아키텍처 설계 및 Kafka 이벤트 파이프라인 구현")
        String roleSummary,

        @Schema(description = "대표 기술 스택", example = "[\"Spring Boot\", \"Kafka\", \"PostgreSQL\"]")
        List<String> techStack,

        @Schema(description = "성과 지표 목록", example = "[\"TPS 300→1200\", \"장애 복구 시간 40% 단축\"]")
        List<String> metrics,

        @Schema(description = "연차 또는 경력 레벨 라벨", example = "3년차")
        String yearLevel,

        @Schema(description = "학교/학력 그룹 라벨", example = "수도권 4년제")
        String schoolGroupLabel,

        @Schema(description = "AI 평가 점수")
        ScoresDto scores,

        @Schema(description = "엘리트 카드 여부 (총점 90점 이상)", example = "false")
        boolean isElite,

        @Schema(description = "카드 생성 시각 (UTC ISO-8601)")
        Instant createdAt
) {
    @Schema(description = "AI 평가 점수 요약")
    public record ScoresDto(
            @Schema(description = "총점 (0~100)", example = "82")
            int total,

            @Schema(description = "레이더 차트용 축별 점수 목록", example = "[4.1, 4.1, 4.1, 4.1, 4.1]")
            List<Double> axes
    ) {}

    public static FeedItemResponse from(AnonymousCard card, User user) {
        int total = card.getTotalScore();
        List<Double> axes = List.of(
                total / 20.0, total / 20.0, total / 20.0, total / 20.0, total / 20.0
        );
        Map<String, Object> avatar = user.getAvatarUrl() != null
                ? Map.of("type", "url", "url", user.getAvatarUrl())
                : Map.of("type", "gradient", "seed", Math.abs(user.getHandle().hashCode() % 10));
        return new FeedItemResponse(
                card.getId(), user.getHandle(), avatar,
                card.getSummary(), card.getRoleSummary(),
                card.getTechStack(), card.getMetrics(),
                card.getYearLevel(), card.getSchoolGroupLabel(),
                new ScoresDto(total, axes),
                total >= 90,
                card.getCreatedAt()
        );
    }
}
