package com.pori.portfolio.controller;

import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import com.pori.portfolio.dto.*;
import com.pori.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PortfolioCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                portfolioService.create(principal.userId(), request)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PortfolioSummaryResponse>>> getMyPortfolios(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(portfolioService.getMyPortfolios(principal.userId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioDetailResponse>> getDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(
                portfolioService.getMyPortfolioDetail(principal.userId(), id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestBody PortfolioUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                portfolioService.update(principal.userId(), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        portfolioService.delete(principal.userId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/republish")
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>> republish(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody PortfolioCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                portfolioService.republish(principal.userId(), id, request)));
    }
}
