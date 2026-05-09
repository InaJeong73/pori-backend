package com.pori.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "포트폴리오 수정 요청. 변경할 필드만 포함하면 됩니다. AI 재평가는 일어나지 않습니다.")
public record PortfolioUpdateRequest(
        @Schema(description = "포트폴리오 제목", example = "결제 MSA 전환, TPS 300 → 1200 달성")
        String title,

        @Schema(description = "상세 소개글", example = "Spring Boot 기반 모놀리스를 MSA로 전환하여 처리량을 4배 향상시켰습니다.")
        String intro,

        @Schema(description = "기술 스택 목록", example = "[\"Spring Boot\", \"Kafka\", \"PostgreSQL\"]")
        List<String> techStack,

        @Schema(description = "공개 범위. 1=전체공개, 2=링크공개, 3=비공개", example = "1")
        short privacyLevel
) {}
