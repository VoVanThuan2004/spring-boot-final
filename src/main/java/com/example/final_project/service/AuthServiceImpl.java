package com.example.final_project.service;

import com.example.final_project.dto.TokenPayload;
import com.example.final_project.dto.request.UserLogin;
import com.example.final_project.dto.response.UserResponse;
import com.example.final_project.entity.RefreshToken;
import com.example.final_project.entity.Role;
import com.example.final_project.entity.User;
import com.example.final_project.entity.UserRole;
import com.example.final_project.exception.UnauthorizedException;
import com.example.final_project.repository.UserRepository;
import com.example.final_project.repository.UserRoleRepository;
import com.example.final_project.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResult login(UserLogin userLogin) {
        User user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is locked");
        }
        if (userLogin.getPassword() == null
                || !passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        Role role = resolveRole(user);
        TokenPayload payload = buildPayload(user, role);
        String accessToken = jwtUtil.generateAccessToken(payload);
        String refreshToken = refreshTokenService.issue(user, payload);

        return new AuthResult(accessToken, refreshToken, jwtUtil.getRefreshTokenExpirationMs(), mapUser(user));
    }

    @Override
    @Transactional
    public AuthResult refresh(String rawRefreshToken) {
        RefreshToken stored = refreshTokenService.validateForRotation(rawRefreshToken);
        User user = stored.getUser();

        if (!user.isActive()) {
            refreshTokenService.revokeAllForUser(user.getId());
            throw new UnauthorizedException("User account is locked");
        }

        Role role = resolveRole(user);
        refreshTokenService.revoke(stored);

        TokenPayload payload = buildPayload(user, role);
        String accessToken = jwtUtil.generateAccessToken(payload);
        String refreshToken = refreshTokenService.issue(user, payload);

        return new AuthResult(accessToken, refreshToken, jwtUtil.getRefreshTokenExpirationMs(), mapUser(user));
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    private Role resolveRole(User user) {
        UserRole userRole = userRoleRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UnauthorizedException("User role not found"));
        if (userRole.getRole() == null) {
            throw new UnauthorizedException("User role not found");
        }
        return userRole.getRole();
    }

    private TokenPayload buildPayload(User user, Role role) {
        return TokenPayload.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(role.getName())
                .build();
    }

    private UserResponse mapUser(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .image(user.getImage())
                .active(user.isActive())
                .build();
    }
}
