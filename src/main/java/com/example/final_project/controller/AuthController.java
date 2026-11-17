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
        LoginResponse loginResponse = authService.login(response, userLogin);

        return ResponseEntity.ok(ApiResponse.success("Login successfully", loginResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.refresh(request, response);

            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", loginResponse));
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
}
