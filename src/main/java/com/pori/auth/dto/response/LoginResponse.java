package com.pori.auth.dto.response;

import com.pori.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {
}
