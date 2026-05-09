package com.pori.comment.controller;

import com.pori.comment.domain.CommentType;
import com.pori.comment.dto.CommentCreateRequest;
import com.pori.comment.dto.CommentResponse;
import com.pori.comment.service.CommentService;
import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/portfolios/{portfolioId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @PathVariable UUID portfolioId,
            @RequestParam(required = false) CommentType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(commentService.getComments(portfolioId, type, page, size)));
    }

    @PostMapping("/portfolios/{portfolioId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID portfolioId,
            @Valid @RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                commentService.create(principal.userId(), portfolioId, request)));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID commentId) {
        commentService.delete(principal.userId(), commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<ApiResponse<Object>> report(@PathVariable UUID commentId) {
        commentService.report(commentId);
        return ResponseEntity.ok(ApiResponse.empty());
    }
}
