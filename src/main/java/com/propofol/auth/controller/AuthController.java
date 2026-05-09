package com.propofol.auth.controller;

import com.propofol.auth.dto.request.LoginRequest;
import com.propofol.auth.dto.request.RegisterRequest;
import com.propofol.auth.dto.response.LoginResponse;
import com.propofol.auth.dto.response.ReissueResponse;
import com.propofol.auth.service.AuthService;
import com.propofol.global.response.ApiResponse;
import com.propofol.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Mock 인증 API")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Mock 회원가입", description = "이메일과 비밀번호로 테스트 사용자를 생성합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(authService.register(request)));
    }

    @Operation(summary = "Mock 로그인", description = "테스트 사용자로 JWT access token과 refresh token을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.accessToken())
                .body(ApiResponse.ok(response));
    }

    @Operation(summary = "Access token 재발급", description = "refresh token으로 access token을 재발급합니다.")
    @PostMapping("/reissue")
    public ApiResponse<ReissueResponse> reissue(@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {
        return ApiResponse.ok(authService.reissue(refreshToken));
    }
}
