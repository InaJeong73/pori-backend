package com.pori.portfolio.controller;

import com.pori.global.response.ApiResponse;
import com.pori.portfolio.dto.request.PortfolioCreateRequest;
import com.pori.portfolio.dto.response.PortfolioDetailResponse;
import com.pori.portfolio.dto.response.PortfolioSummaryResponse;
import com.pori.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public ApiResponse<List<PortfolioSummaryResponse>> list(
            @RequestParam(required = false) String jobCategory,
            @RequestParam(required = false) Integer minScore
    ) {
        return ApiResponse.ok(portfolioService.getPublicList(jobCategory, minScore));
    }

    @GetMapping("/{id}")
    public ApiResponse<PortfolioDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(portfolioService.getDetail(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PortfolioDetailResponse> create(@Valid @RequestBody PortfolioCreateRequest request) {
        return ApiResponse.created(portfolioService.create(request));
    }
}
