package com.pori.techstack.controller;

import com.pori.global.response.ApiResponse;
import com.pori.techstack.dto.TechStackOptionResponse;
import com.pori.techstack.repository.TechStackOptionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "TechStack", description = "기술 스택 옵션 조회")
@RestController
@RequestMapping("/tech-stack")
public class TechStackController {

    private final TechStackOptionRepository repository;

    public TechStackController(TechStackOptionRepository repository) {
        this.repository = repository;
    }

    @Operation(
            summary = "기술 스택 옵션 목록 조회",
            description = "포트폴리오 등록/수정 시 선택 가능한 기술 스택 옵션을 카테고리와 함께 반환합니다. 인증 불필요."
    )
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<List<TechStackOptionResponse>>> getOptions() {
        List<TechStackOptionResponse> options = repository.findAll().stream()
                .map(TechStackOptionResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(options));
    }
}
