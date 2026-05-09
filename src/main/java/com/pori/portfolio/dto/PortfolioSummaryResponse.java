package com.pori.portfolio.dto;

import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.domain.PortfolioStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PortfolioSummaryResponse(
        UUID id,
        String title,
        List<String> techStack,
        PortfolioStatus status,
        short privacyLevel,
        int version,
        Instant createdAt,
        Instant publishedAt
) {
    public static PortfolioSummaryResponse from(Portfolio p) {
        return new PortfolioSummaryResponse(p.getId(), p.getTitle(), p.getTechStack(),
                p.getStatus(), p.getPrivacyLevel(), p.getVersion(), p.getCreatedAt(), p.getPublishedAt());
    }
}
