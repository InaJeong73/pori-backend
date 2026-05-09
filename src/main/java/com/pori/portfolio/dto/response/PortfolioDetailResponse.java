package com.pori.portfolio.dto.response;

import com.pori.portfolio.domain.CategoryScores;
import com.pori.portfolio.domain.Portfolio;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioDetailResponse(
        Long id,
        String authorName,
        String title,
        String jobCategory,
        String portfolioUrl,
        List<String> skills,
        String mainProject,
        String role,
        String problemSolved,
        String techReason,
        String result,
        String githubUrl,
        String deployUrl,
        String summary,
        int poriScore,
        String grade,
        boolean isPublic,
        boolean isTopReference,
        boolean isHighTrust,
        CategoryScores categoryScores,
        List<String> strengths,
        List<String> improvements,
        String recommendation,
        LocalDateTime createdAt
) {
    public static PortfolioDetailResponse from(Portfolio p) {
        return new PortfolioDetailResponse(
                p.getId(),
                p.getAuthorName(),
                p.getTitle(),
                p.getJobCategory().name().toLowerCase(),
                p.getPortfolioUrl(),
                p.getSkillList(),
                p.getMainProject(),
                p.getRole(),
                p.getProblemSolved(),
                p.getTechReason(),
                p.getResult(),
                p.getGithubUrl(),
                p.getDeployUrl(),
                p.getSummary(),
                p.getPoriScore(),
                p.getGrade().display(),
                p.isPublic(),
                p.isTopReference(),
                p.isHighTrust(),
                p.getCategoryScores(),
                p.getStrengthList(),
                p.getImprovementList(),
                p.getRecommendation(),
                p.getCreatedAt()
        );
    }
}
