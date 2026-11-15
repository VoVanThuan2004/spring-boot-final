package com.example.final_project.util;

import com.example.final_project.dto.TokenPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_FULL_NAME = "fullName";
    private static final String CLAIM_ROLE = "role";
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-token-expiration-ms:900000}") long accessTokenExpirationMs,
                   @Value("${jwt.refresh-token-expiration-ms:604800000}") long refreshTokenExpirationMs) {
        byte[] secretBytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "JWT secret must be at least " + MIN_SECRET_BYTES + " bytes (256 bits) long");
        }
        if (accessTokenExpirationMs <= 0 || refreshTokenExpirationMs <= 0) {
            throw new IllegalStateException("JWT expiration values must be positive");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(TokenPayload payload) {
        return generateToken(payload, TOKEN_TYPE_ACCESS, accessTokenExpirationMs);
    }

    public String generateRefreshToken(TokenPayload payload) {
        return generateToken(payload, TOKEN_TYPE_REFRESH, refreshTokenExpirationMs);
    }

    private String generateToken(TokenPayload payload, String type, long expirationMs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(payload.getUserId()))
                .id(UUID.randomUUID().toString())
                .claim(CLAIM_FULL_NAME, payload.getFullName())
                .claim(CLAIM_ROLE, payload.getRole())
                .claim(CLAIM_TYPE, type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public TokenPayload validateAccessToken(String token) {
        return validateToken(token, TOKEN_TYPE_ACCESS);
    }

    public TokenPayload validateRefreshToken(String token) {
        return validateToken(token, TOKEN_TYPE_REFRESH);
    }

    private TokenPayload validateToken(String token, String expectedType) {
        Claims claims = parseClaims(token);
        String type = (String) claims.get(CLAIM_TYPE);
        if (!expectedType.equals(type)) {
            throw new JwtException("Invalid token type");
        }
        return toTokenPayload(claims);
    }

    public TokenPayload getTokenPayload(String token) {
        return toTokenPayload(parseClaims(token));
    }

    private TokenPayload toTokenPayload(Claims claims) {
        return TokenPayload.builder()
                .userId(Integer.parseInt(claims.getSubject()))
                .fullName((String) claims.get(CLAIM_FULL_NAME))
                .role((String) claims.get(CLAIM_ROLE))
                .build();
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }
}
