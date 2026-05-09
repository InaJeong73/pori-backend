package com.pori.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT 토큰 응답")
public record TokenResponse(
        @Schema(description = "액세스 토큰 (유효기간 15분). Swagger Authorize에 이 값을 입력하세요.", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,

        @Schema(description = "리프레시 토큰 (유효기간 14일). 액세스 토큰 만료 시 /auth/refresh에 사용하세요.", example = "eyJhbGciOiJIUzI1NiJ9...")
        String refreshToken
) {}
