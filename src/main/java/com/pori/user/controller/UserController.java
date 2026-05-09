package com.pori.user.controller;

import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import com.pori.user.dto.UpdateUserRequest;
import com.pori.user.dto.UserResponse;
import com.pori.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMe(principal.userId())));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(@AuthenticationPrincipal UserPrincipal principal,
                                                               @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateMe(principal.userId(), request)));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Object>> deleteMe(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteMe(principal.userId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/handles/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkHandle(@RequestParam String handle) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("available", userService.isHandleAvailable(handle))));
    }
}
