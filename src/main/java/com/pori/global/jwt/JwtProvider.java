package com.pori.global.jwt;

import com.pori.global.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JwtProvider(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = props.accessTokenExpiry();
        this.refreshTokenExpiry = props.refreshTokenExpiry();
    }

    public String createAccessToken(UUID userId, String handle) {
        return buildToken(userId.toString(), handle, accessTokenExpiry);
    }

    public String createRefreshToken(UUID userId, String handle) {
        return buildToken(userId.toString(), handle, refreshTokenExpiry);
    }

    private String buildToken(String subject, String handle, long expiry) {
        Date now = new Date();
        return Jwts.builder()
                .subject(subject)
                .claim("handle", handle)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiry))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
