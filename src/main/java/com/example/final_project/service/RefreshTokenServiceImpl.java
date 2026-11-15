package com.example.final_project.service;

import com.example.final_project.dto.TokenPayload;
import com.example.final_project.entity.RefreshToken;
import com.example.final_project.entity.User;
import com.example.final_project.exception.UnauthorizedException;
import com.example.final_project.repository.RefreshTokenRepository;
import com.example.final_project.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private static final int MAX_TOKEN_LENGTH = 4096;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public String issue(User user, TokenPayload payload) {
        String rawRefreshToken = jwtUtil.generateRefreshToken(payload);
        Instant now = Instant.now();
        refreshTokenRepository.save(RefreshToken.builder()
                .tokenHash(hash(rawRefreshToken))
                .user(user)
                .expiresAt(now.plusMillis(jwtUtil.getRefreshTokenExpirationMs()))
                .revoked(false)
                .createdAt(now)
                .build());
        return rawRefreshToken;
    }

    @Override
    @Transactional
    public RefreshToken validateForRotation(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token is missing");
        }
        try {
            jwtUtil.validateRefreshToken(rawRefreshToken);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid or expired refresh token", ex);
        }

        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (stored.isRevoked()) {
            revokeAllForUser(stored.getUser().getId());
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            revoke(stored);
            throw new UnauthorizedException("Refresh token has expired");
        }
        return stored;
    }

    @Override
    @Transactional
    public void revoke(RefreshToken refreshToken) {
        if (refreshToken.isRevoked()) {
            return;
        }
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public void revoke(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank() || rawRefreshToken.length() > MAX_TOKEN_LENGTH) {
            return;
        }
        refreshTokenRepository.findByTokenHash(hash(rawRefreshToken)).ifPresent(this::revoke);
    }

    @Override
    @Transactional
    public void revokeAllForUser(int userId) {
        refreshTokenRepository.revokeAllByUserId(userId, Instant.now());
    }

    private String hash(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available", ex);
        }
    }
}
