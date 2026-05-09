package com.pori.auth.controller;

import com.pori.auth.dto.LoginRequest;
import com.pori.auth.dto.RefreshRequest;
import com.pori.auth.dto.RegisterRequest;
import com.pori.auth.dto.TokenResponse;
import com.pori.auth.service.AuthService;
import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import com.pori.user.dto.UserResponse;
import com.pori.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "회원가입 · 로그인 · 토큰 갱신 · 내 정보 조회")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(
            summary = "회원가입",
            description = "이메일·비밀번호·핸들로 계정을 생성하고 JWT 토큰을 즉시 발급합니다. 인증 불필요."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(authService.register(request)));
    }

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다. 인증 불필요."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @Operation(
            summary = "토큰 갱신",
            description = "만료된 accessToken 대신 refreshToken으로 새 토큰 쌍을 발급받습니다. refreshToken 유효기간은 14일입니다."
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(request.refreshToken())));
    }

    @Operation(
            summary = "내 정보 조회",
            description = "accessToken으로 현재 로그인된 사용자의 프로필을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMe(principal.userId())));
    }
}
