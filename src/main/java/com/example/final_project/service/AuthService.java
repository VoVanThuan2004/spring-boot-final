package com.example.final_project.service;

import com.example.final_project.dto.request.UserLogin;
import com.example.final_project.dto.response.UserResponse;

public interface AuthService {
    AuthResult login(UserLogin userLogin);

    AuthResult refresh(String rawRefreshToken);

    void logout(String rawRefreshToken);

    record AuthResult(String accessToken, String refreshToken, long refreshTokenExpiresInMs, UserResponse user) {
    }
}
