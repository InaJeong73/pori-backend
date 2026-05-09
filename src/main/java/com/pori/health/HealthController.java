package com.pori.health;

import com.pori.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@Tag(name = "Health", description = "상태 확인 API")
@RestController
public class HealthController {

    @Operation(summary = "Health check", description = "서버 상태 확인용 API입니다.")
    @GetMapping("/health")
    public ApiResponse<HealthResponse> health() {
        return ApiResponse.ok(new HealthResponse("UP", OffsetDateTime.now()));
    }

    public record HealthResponse(
            String status,
            OffsetDateTime timestamp
    ) {
    }
}
