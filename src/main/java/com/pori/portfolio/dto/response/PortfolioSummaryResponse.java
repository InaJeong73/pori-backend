package com.pori.portfolio.dto.response;

import com.pori.portfolio.domain.Portfolio;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioSummaryResponse(
        Long id,
        String authorName,
        String title,
        String jobCategory,
        String portfolioUrl,
        List<String> skills,
        String summary,
        int poriScore,
        String grade,
        boolean isTopReference,
        boolean isHighTrust,
        LocalDateTime createdAt
) {
    public static PortfolioSummaryResponse from(Portfolio p) {
        return new PortfolioSummaryResponse(
                p.getId(),
                p.getAuthorName(),
                p.getTitle(),
                p.getJobCategory().name().toLowerCase(),
                p.getPortfolioUrl(),
                p.getSkillList(),
                p.getSummary(),
                p.getPoriScore(),
                p.getGrade().display(),
                p.isTopReference(),
                p.isHighTrust(),
                p.getCreatedAt()
        );
    }
}
