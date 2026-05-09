package com.pori.portfolio.dto.request;

import com.pori.portfolio.domain.JobCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PortfolioCreateRequest(
        @NotBlank String authorName,
        @NotBlank String title,
        @NotNull JobCategory jobCategory,
        @NotBlank String portfolioUrl,
        @NotNull List<String> skills,
        @NotBlank String mainProject,
        @NotBlank String role,
        String problemSolved,
        String techReason,
        String result,
        String githubUrl,
        String deployUrl,
        @NotBlank String summary
) {}
