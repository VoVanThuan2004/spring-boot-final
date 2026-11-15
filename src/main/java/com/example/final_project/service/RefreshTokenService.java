package com.example.final_project.service;

import com.example.final_project.dto.TokenPayload;
import com.example.final_project.entity.RefreshToken;
import com.example.final_project.entity.User;

public interface RefreshTokenService {
    String issue(User user, TokenPayload payload);

    RefreshToken validateForRotation(String rawRefreshToken);

    void revoke(RefreshToken refreshToken);

    void revoke(String rawRefreshToken);

    void revokeAllForUser(int userId);
}
