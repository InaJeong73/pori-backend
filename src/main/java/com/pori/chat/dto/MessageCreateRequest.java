package com.pori.chat.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MessageCreateRequest(
        @NotBlank String body,
        UUID attachedPortfolioId
) {}
