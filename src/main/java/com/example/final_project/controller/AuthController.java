package com.example.final_project.controller;

import com.example.final_project.dto.request.UserLogin;
import com.example.final_project.dto.response.ApiResponse;
import com.example.final_project.dto.response.LoginResponse;
import com.example.final_project.exception.UnauthorizedException;
import com.example.final_project.security.RefreshTokenCookieService;
import com.example.final_project.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenCookieService refreshTokenCookieService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody UserLogin userLogin,
                                                            HttpServletResponse response) {
        AuthService.AuthResult result = authService.login(userLogin);
        refreshTokenCookieService.addRefreshTokenCookie(response, result.refreshToken(),
                result.refreshTokenExpiresInMs());
        return ResponseEntity.ok(ApiResponse.success("Login successfully", toLoginResponse(result)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            AuthService.AuthResult result = authService.refresh(
                    refreshTokenCookieService.getRefreshToken(request));
            refreshTokenCookieService.addRefreshTokenCookie(response, result.refreshToken(),
                    result.refreshTokenExpiresInMs());
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", toLoginResponse(result)));
        } catch (UnauthorizedException ex) {
            refreshTokenCookieService.clearRefreshTokenCookie(response);
            throw ex;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(refreshTokenCookieService.getRefreshToken(request));
        refreshTokenCookieService.clearRefreshTokenCookie(response);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    private LoginResponse toLoginResponse(AuthService.AuthResult result) {
        return LoginResponse.builder()
                .accessToken(result.accessToken())
                .user(result.user())
                .build();
    }
}
