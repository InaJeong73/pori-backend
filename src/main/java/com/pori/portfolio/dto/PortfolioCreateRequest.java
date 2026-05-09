package com.pori.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PortfolioCreateRequest(
        @NotBlank String title,
        String intro,
        @Size(max = 20) List<String> techStack,
        short privacyLevel
) {}
