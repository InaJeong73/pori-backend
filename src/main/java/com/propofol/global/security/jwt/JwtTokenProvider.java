package com.propofol.global.security.jwt;

import com.propofol.global.config.JwtProperties;
import com.propofol.global.security.AuthPrincipal;
import com.propofol.user.domain.User;
import com.propofol.user.domain.type.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final String ROLE_CLAIM = "role";

    private final JwtProperties jwtProperties;
    private Key key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    void initializeKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(User user) {
        return createToken(user, jwtProperties.accessTokenExpiration());
    }

    public String createRefreshToken(User user) {
        return createToken(user, jwtProperties.refreshTokenExpiration());
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public AuthPrincipal getPrincipal(String token) {
        Claims claims = parseClaims(token);
        return new AuthPrincipal(Long.valueOf(claims.getSubject()), Role.valueOf(claims.get(ROLE_CLAIM, String.class)));
    }

    private String createToken(User user, long expirationMillis) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiresAt = new Date(now + expirationMillis);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.issuer())
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .setSubject(user.getId().toString())
                .addClaims(Map.of(ROLE_CLAIM, user.getRole().name()))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
