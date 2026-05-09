package com.pori.portfolio.controller;

import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import com.pori.portfolio.dto.*;
import com.pori.portfolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Portfolio", description = "포트폴리오 등록 · 조회 · 수정 · 삭제 · 재게시")
@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Operation(
            summary = "포트폴리오 등록",
            description = """
                    새 포트폴리오를 등록합니다. 등록 즉시 AI 평가가 비동기로 시작되며, status가 PENDING → EVALUATING → PUBLISHED(또는 FAILED)로 변경됩니다.
                    PUBLISHED 상태가 되어야 피드에 공개됩니다.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PortfolioCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                portfolioService.create(principal.userId(), request)));
    }

    @Operation(
            summary = "내 포트폴리오 목록 조회",
            description = "로그인된 사용자의 포트폴리오 전체 목록을 요약 형태로 반환합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PortfolioSummaryResponse>>> getMyPortfolios(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(portfolioService.getMyPortfolios(principal.userId())));
    }

    @Operation(
            summary = "포트폴리오 상세 조회",
            description = "포트폴리오 ID로 상세 정보와 AI 평가 결과를 조회합니다. 본인 포트폴리오만 조회 가능합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioDetailResponse>> getDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "포트폴리오 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(
                portfolioService.getMyPortfolioDetail(principal.userId(), id)));
    }

    @Operation(
            summary = "포트폴리오 수정",
            description = "제목, 소개글, 기술 스택, 공개 범위를 수정합니다. 변경할 필드만 전달하면 됩니다. AI 재평가는 일어나지 않습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "포트폴리오 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @RequestBody PortfolioUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                portfolioService.update(principal.userId(), id, request)));
    }

    @Operation(
            summary = "포트폴리오 삭제",
            description = "포트폴리오와 연관된 AI 평가 데이터를 영구 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "포트폴리오 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        portfolioService.delete(principal.userId(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "포트폴리오 재게시",
            description = """
                    기존 포트폴리오를 ARCHIVED 처리하고 새 내용으로 다시 게시합니다.
                    AI 평가가 새로 시작되며 version이 1 증가합니다.
                    FAILED 상태 포트폴리오를 수정·재도전할 때 사용하세요.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/republish")
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>> republish(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "재게시할 포트폴리오 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @Valid @RequestBody PortfolioCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                portfolioService.republish(principal.userId(), id, request)));
    }
}
