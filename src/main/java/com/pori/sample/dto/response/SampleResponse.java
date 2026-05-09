package com.pori.sample.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "샘플 응답")
public record SampleResponse(
        Long id,
        String title,
        String content
) {
}
