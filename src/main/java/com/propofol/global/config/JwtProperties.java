package com.propofol.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secretKey,
        String issuer,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {
}
