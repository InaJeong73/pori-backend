package com.pori.portfolio.dto;

import com.pori.portfolio.domain.Evaluation;
import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.domain.PortfolioStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "포트폴리오 상세 응답 (AI 평가 결과 포함)")
public record PortfolioDetailResponse(
        @Schema(description = "포트폴리오 UUID") UUID id,
        @Schema(description = "제목") String title,
        @Schema(description = "상세 소개글") String intro,
        @Schema(description = "기술 스택 목록") List<String> techStack,
        @Schema(description = "상태. PENDING / EVALUATING / PUBLISHED / FAILED / ARCHIVED") PortfolioStatus status,
        @Schema(description = "공개 범위. 1=전체공개, 2=링크공개, 3=비공개") short privacyLevel,
        @Schema(description = "버전 (재게시 시 증가)") int version,
        @Schema(description = "최초 생성 시각 (UTC ISO-8601)") Instant createdAt,
        @Schema(description = "게시 완료 시각 (UTC ISO-8601). PUBLISHED 상태가 아니면 null") Instant publishedAt,
        @Schema(description = "AI 평가 결과. PUBLISHED 상태가 아니면 null") EvaluationDto evaluation
) {
    @Schema(description = "AI 평가 결과")
    public record EvaluationDto(
            @Schema(description = "총점 (0~100)", example = "82") int totalScore,
            @Schema(description = "항목별 점수 JSON 문자열") String scoresJson,
            @Schema(description = "보완이 필요한 카테고리 목록", example = "[\"임팩트\", \"구체성\"]") List<String> weaknessCategories,
            @Schema(description = "개선 제안 JSON 문자열") String improvementsJson,
            @Schema(description = "AI 첫인상 요약", example = "성과 수치가 명확하고 기술 선택 이유가 잘 설명되어 있습니다.") String firstImpression
    ) {
        public static EvaluationDto from(Evaluation e) {
            return new EvaluationDto(e.getTotalScore(), e.getScoresJson(),
                    e.getWeaknessCategories(), e.getImprovementsJson(), e.getFirstImpression());
        }
    }

    public static PortfolioDetailResponse from(Portfolio p, Evaluation eval) {
        return new PortfolioDetailResponse(p.getId(), p.getTitle(), p.getIntro(), p.getTechStack(),
                p.getStatus(), p.getPrivacyLevel(), p.getVersion(), p.getCreatedAt(), p.getPublishedAt(),
                eval != null ? EvaluationDto.from(eval) : null);
    }
}
