package com.pori.portfolio.dto;

import com.pori.portfolio.domain.Evaluation;
import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.domain.PortfolioStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PortfolioDetailResponse(
        UUID id,
        String title,
        String intro,
        List<String> techStack,
        PortfolioStatus status,
        short privacyLevel,
        int version,
        Instant createdAt,
        Instant publishedAt,
        EvaluationDto evaluation
) {
    public record EvaluationDto(
            int totalScore,
            String scoresJson,
            List<String> weaknessCategories,
            String improvementsJson,
            String firstImpression
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
