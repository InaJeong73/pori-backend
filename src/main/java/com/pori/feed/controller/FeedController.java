package com.pori.feed.controller;

import com.pori.feed.dto.FeedItemResponse;
import com.pori.feed.service.FeedService;
import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Feed", description = "익명 포트폴리오 피드 조회")
@RestController
@RequestMapping("/portfolios/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @Operation(
            summary = "익명 포트폴리오 피드 조회",
            description = "공개된 포트폴리오 카드를 필터와 페이지 조건으로 조회합니다. 카드 상세 열람 전 목록 탐색에 사용합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FeedItemResponse>>> getFeed(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "직군 필터. FRONTEND / BACKEND / FULLSTACK / MOBILE / DATA_AI / OTHER", example = "BACKEND")
            @RequestParam(required = false) String job,
            @Parameter(description = "연차 또는 경력 레벨 필터", example = "3")
            @RequestParam(required = false) String year,
            @Parameter(description = "최소 AI 점수", example = "60")
            @RequestParam(defaultValue = "0") int scoreMin,
            @Parameter(description = "최대 AI 점수", example = "100")
            @RequestParam(defaultValue = "100") int scoreMax,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "12")
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                feedService.getFeed(principal.userId(), job, year, scoreMin, scoreMax, page, size)));
    }

    @Operation(
            summary = "익명 포트폴리오 카드 상세 조회",
            description = "피드 카드 UUID로 상세 정보를 조회합니다. Give-to-Get 조건을 충족해야 합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<FeedItemResponse>> getCardDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "조회할 익명 카드 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID cardId) {
        return ResponseEntity.ok(ApiResponse.ok(
                feedService.getCardDetail(principal.userId(), cardId)));
    }
}
