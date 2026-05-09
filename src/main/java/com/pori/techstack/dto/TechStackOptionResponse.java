package com.pori.techstack.dto;

import com.pori.techstack.domain.TechStackOption;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "기술 스택 옵션 응답")
public record TechStackOptionResponse(
        @Schema(description = "저장/요청에 사용하는 기술 스택 키", example = "spring_boot")
        String key,

        @Schema(description = "화면 표시용 기술 스택 이름", example = "Spring Boot")
        String label,

        @Schema(description = "기술 스택 카테고리", example = "Backend")
        String category
) {
    public static TechStackOptionResponse from(TechStackOption o) {
        return new TechStackOptionResponse(o.getKey(), o.getLabel(), o.getCategory());
    }
}
