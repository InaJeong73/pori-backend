package com.pori.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
        List<String> allowedOrigins,
        List<String> allowedOriginPatterns
) {
    public CorsProperties {
        allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
        allowedOriginPatterns = withCloudRunSwaggerOrigin(allowedOriginPatterns);
    }

    private static List<String> withCloudRunSwaggerOrigin(List<String> configuredPatterns) {
        List<String> patterns = new ArrayList<>(
                configuredPatterns == null ? List.of() : configuredPatterns
        );
        String cloudRunOrigin = "https://pori-be-*.asia-northeast3.run.app";
        if (!patterns.contains(cloudRunOrigin)) {
            patterns.add(cloudRunOrigin);
        }
        return List.copyOf(patterns);
    }
}
