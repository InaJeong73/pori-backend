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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(request.refreshToken())));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMe(principal.userId())));
    }
}
