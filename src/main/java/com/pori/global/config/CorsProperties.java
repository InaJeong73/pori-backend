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
        allowedOrigins = withDeployedFrontendOrigin(allowedOrigins);
        allowedOriginPatterns = withCloudRunSwaggerOrigin(allowedOriginPatterns);
    }

    private static List<String> withDeployedFrontendOrigin(List<String> configuredOrigins) {
        List<String> origins = new ArrayList<>(
                configuredOrigins == null ? List.of() : configuredOrigins
        );
        String frontendOrigin = "https://pori-five.vercel.app";
        if (!origins.contains(frontendOrigin)) {
            origins.add(frontendOrigin);
        }
        return List.copyOf(origins);
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
