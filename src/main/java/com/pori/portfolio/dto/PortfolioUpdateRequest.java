package com.pori.portfolio.dto;

import java.util.List;

public record PortfolioUpdateRequest(
        String title,
        String intro,
        List<String> techStack,
        short privacyLevel
) {}
