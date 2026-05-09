package com.pori.portfolio.dto;

import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.domain.PortfolioStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "포트폴리오 요약 응답")
public record PortfolioSummaryResponse(
        @Schema(description = "포트폴리오 UUID") UUID id,
        @Schema(description = "제목", example = "결제 MSA 전환, TPS 300 → 1200 달성") String title,
        @Schema(description = "기술 스택 목록", example = "[\"Spring Boot\", \"Kafka\"]") List<String> techStack,
        @Schema(description = "상태. PENDING=대기, EVALUATING=평가중, PUBLISHED=게시됨, FAILED=평가실패, ARCHIVED=보관", example = "PUBLISHED") PortfolioStatus status,
        @Schema(description = "공개 범위. 1=전체공개, 2=링크공개, 3=비공개", example = "1") short privacyLevel,
        @Schema(description = "버전 (재게시 시 증가)", example = "1") int version,
        @Schema(description = "최초 생성 시각 (UTC ISO-8601)") Instant createdAt,
        @Schema(description = "게시 완료 시각 (UTC ISO-8601). PUBLISHED 상태가 아니면 null") Instant publishedAt
) {
    public static PortfolioSummaryResponse from(Portfolio p) {
        return new PortfolioSummaryResponse(p.getId(), p.getTitle(), p.getTechStack(),
                p.getStatus(), p.getPrivacyLevel(), p.getVersion(), p.getCreatedAt(), p.getPublishedAt());
    }
}
