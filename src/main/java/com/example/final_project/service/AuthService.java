package com.example.final_project.service;

import com.example.final_project.dto.request.UserLogin;
import com.example.final_project.dto.response.LoginResponse;
import com.example.final_project.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse login(HttpServletResponse httpResponse, UserLogin userLogin);

    LoginResponse refresh(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    void logout(String rawRefreshToken);

    record AuthResult(String accessToken, String refreshToken, long refreshTokenExpiresInMs, UserResponse user) {
    }
}
