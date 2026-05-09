package com.pori.user.controller;

import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import com.pori.user.dto.UpdateUserRequest;
import com.pori.user.dto.UserResponse;
import com.pori.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "User", description = "사용자 프로필 조회 · 수정 · 탈퇴 · 핸들 중복 확인")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "내 프로필 조회",
            description = "로그인된 사용자의 전체 프로필 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMe(principal.userId())));
    }

    @Operation(
            summary = "내 프로필 수정",
            description = "핸들, 표시 이름, 직군, 연차, 학력, 한 줄 소개, 공개 범위 등을 수정합니다. 변경할 필드만 전달하면 됩니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(@AuthenticationPrincipal UserPrincipal principal,
                                                               @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateMe(principal.userId(), request)));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "계정과 관련 데이터를 모두 삭제합니다. 되돌릴 수 없습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Object>> deleteMe(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteMe(principal.userId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "핸들 중복 확인",
            description = "사용하려는 핸들이 이미 사용 중인지 확인합니다. `available: true`이면 사용 가능합니다. 인증 불필요."
    )
    @GetMapping("/handles/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkHandle(
            @Parameter(description = "확인할 핸들 (영문·숫자·언더스코어, 3~20자)", example = "alice_fe")
            @RequestParam String handle) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("available", userService.isHandleAvailable(handle))));
    }
}
