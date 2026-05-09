package com.pori.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "포트폴리오 등록 요청")
public record PortfolioCreateRequest(
        @Schema(description = "포트폴리오 제목. 핵심 성과와 수치를 포함하면 AI 평가 점수가 높아집니다.", example = "결제 MSA 전환, TPS 300 → 1200 달성")
        @NotBlank String title,

        @Schema(description = "상세 소개글. 본인 역할, 기술적 도전, 성과를 구체적으로 작성하세요.", example = "Spring Boot 기반 모놀리스를 MSA로 전환. Kafka 기반 이벤트 드리븐 아키텍처 설계 및 구현.")
        String intro,

        @Schema(description = "사용 기술 스택 목록 (최대 20개). /tech-stack/options API로 사용 가능한 값을 확인하세요.", example = "[\"Spring Boot\", \"Kafka\", \"PostgreSQL\", \"Docker\"]")
        @Size(max = 20) List<String> techStack,

        @Schema(description = "공개 범위. 1=전체공개(피드 노출), 2=링크공개, 3=비공개", example = "1")
        short privacyLevel
) {}
