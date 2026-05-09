package com.pori.comment.controller;

import com.pori.comment.domain.CommentType;
import com.pori.comment.dto.CommentCreateRequest;
import com.pori.comment.dto.CommentResponse;
import com.pori.comment.service.CommentService;
import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Comment", description = "포트폴리오 댓글 조회 · 작성 · 삭제 · 신고")
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(
            summary = "포트폴리오 댓글 목록 조회",
            description = "포트폴리오에 달린 댓글을 최신순 페이지로 조회합니다. type을 지정하면 칭찬, 제안, 질문 댓글만 필터링합니다."
    )
    @GetMapping("/portfolios/{portfolioId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @Parameter(description = "댓글을 조회할 포트폴리오 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID portfolioId,
            @Parameter(description = "댓글 유형 필터. PRAISE / SUGGEST / QUESTION", example = "PRAISE")
            @RequestParam(required = false) CommentType type,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(commentService.getComments(portfolioId, type, page, size)));
    }

    @Operation(
            summary = "포트폴리오 댓글 작성",
            description = "다른 사용자의 포트폴리오에 칭찬, 제안, 질문 댓글을 작성합니다. 본인 포트폴리오에는 작성할 수 없습니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/portfolios/{portfolioId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "댓글을 작성할 포트폴리오 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID portfolioId,
            @Valid @RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                commentService.create(principal.userId(), portfolioId, request)));
    }

    @Operation(
            summary = "댓글 삭제",
            description = "본인이 작성한 댓글을 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "삭제할 댓글 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID commentId) {
        commentService.delete(principal.userId(), commentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "댓글 신고",
            description = "부적절한 댓글을 신고합니다. 신고 횟수가 증가합니다."
    )
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<ApiResponse<Object>> report(
            @Parameter(description = "신고할 댓글 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID commentId) {
        commentService.report(commentId);
        return ResponseEntity.ok(ApiResponse.empty());
    }
}
