package com.pori.user.dto.response;

import com.pori.user.domain.User;
import com.pori.user.domain.type.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 응답")
public record UserResponse(
        Long id,
        String email,
        String nickname,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }
}
