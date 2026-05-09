package com.pori.sample.controller;

import com.pori.global.response.ApiResponse;
import com.pori.sample.dto.request.SampleRequest;
import com.pori.sample.dto.response.SampleResponse;
import com.pori.sample.service.SampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Sample", description = "샘플 CRUD API")
@RestController
@RequestMapping("/samples")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Operation(summary = "샘플 목록 조회")
    @GetMapping
    public ApiResponse<List<SampleResponse>> findAll() {
        return ApiResponse.ok(sampleService.findAll());
    }

    @Operation(summary = "샘플 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<SampleResponse>> create(@Valid @RequestBody SampleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(sampleService.create(request)));
    }

    @Operation(summary = "샘플 단건 조회")
    @GetMapping("/{id}")
    public ApiResponse<SampleResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(sampleService.findById(id));
    }

    @Operation(summary = "샘플 수정")
    @PutMapping("/{id}")
    public ApiResponse<SampleResponse> update(@PathVariable Long id, @Valid @RequestBody SampleRequest request) {
        return ApiResponse.ok(sampleService.update(id, request));
    }

    @Operation(summary = "샘플 삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@PathVariable Long id) {
        sampleService.delete(id);
        return ApiResponse.empty();
    }
}
