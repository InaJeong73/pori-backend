package com.propofol.user.controller;

import com.propofol.global.response.ApiResponse;
import com.propofol.global.security.AuthPrincipal;
import com.propofol.user.dto.response.UserResponse;
import com.propofol.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "내 정보 조회", description = "Authorization header의 JWT로 현재 사용자를 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal AuthPrincipal principal) {
        return ApiResponse.ok(UserResponse.from(userService.findById(principal.userId())));
    }
}
