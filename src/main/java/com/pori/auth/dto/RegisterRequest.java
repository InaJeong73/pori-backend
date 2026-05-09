package com.pori.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record RegisterRequest(
        @Schema(description = "이메일 주소", example = "alice@pori.dev")
        @Email @NotBlank String email,

        @Schema(description = "비밀번호 (최소 8자)", example = "password123")
        @NotBlank @Size(min = 8) String password,

        @Schema(description = "닉네임. 프로필 표시 이름으로 사용됩니다.", example = "포리유저")
        @NotBlank @Size(max = 30) String nickname
) {}
