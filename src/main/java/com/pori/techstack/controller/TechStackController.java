package com.pori.techstack.controller;

import com.pori.global.response.ApiResponse;
import com.pori.techstack.dto.TechStackOptionResponse;
import com.pori.techstack.repository.TechStackOptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tech-stack")
public class TechStackController {

    private final TechStackOptionRepository repository;

    public TechStackController(TechStackOptionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/options")
    public ResponseEntity<ApiResponse<List<TechStackOptionResponse>>> getOptions() {
        List<TechStackOptionResponse> options = repository.findAll().stream()
                .map(TechStackOptionResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(options));
    }
}
