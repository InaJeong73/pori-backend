package com.pori.feed.controller;

import com.pori.feed.dto.FeedItemResponse;
import com.pori.feed.service.FeedService;
import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/portfolios/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FeedItemResponse>>> getFeed(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String job,
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "0") int scoreMin,
            @RequestParam(defaultValue = "100") int scoreMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                feedService.getFeed(principal.userId(), job, year, scoreMin, scoreMax, page, size)));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<FeedItemResponse>> getCardDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID cardId) {
        return ResponseEntity.ok(ApiResponse.ok(
                feedService.getCardDetail(principal.userId(), cardId)));
    }
}
