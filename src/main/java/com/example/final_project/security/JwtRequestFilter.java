package com.example.final_project.security;

import com.example.final_project.dto.CustomUserDetails;
import com.example.final_project.dto.TokenPayload;
import com.example.final_project.entity.Role;
import com.example.final_project.entity.User;
import com.example.final_project.entity.UserRole;
import com.example.final_project.repository.RoleRepository;
import com.example.final_project.repository.UserRepository;
import com.example.final_project.repository.UserRoleRepository;
import com.example.final_project.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");

        String token = null;
        TokenPayload tokenPayload = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7);
            try {
                tokenPayload = jwtTokenUtil.getTokenPayload(token);
            } catch (ExpiredJwtException e) {
                System.out.println("JWT token expired");
            }

        }
        else {
            System.out.println("Authorization header is missing");
        }

        if (tokenPayload != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> user = userRepository.findById(tokenPayload.getUserId());
            if (user.isPresent()) {
                Optional<UserRole> userRole = userRoleRepository.findByUserId(user.get().getId());
                Optional<Role> role = roleRepository.findById(userRole.get().getRole().getId());
                if (role.isPresent()) {
                    // Chuyen Object User -> TokenPayload
                    TokenPayload tokenPayload1 = TokenPayload.builder()
                            .userId(user.get().getId())
                            .fullName(user.get().getFullName())
                            .role(role.get().getName())
                            .build();

                    if (jwtTokenUtil.isValidToken(token, tokenPayload1)) {

                        // Lưu các role: admin, user
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority(role.get().getName())
                        );

                        // Tạo UserDetail => Lưu vào context holder
                        UserDetails userDetails = new CustomUserDetails(
                                user.get().getId(),
                                user.get().getFullName(),
                                user.get().getPassword(),
                                user.get().getEmail(),
                                authorities
                        );
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                        // Đánh dấu user đã đăng nhập
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }

            }

        }

        filterChain.doFilter(request, response); // Cho user qua cổng
    }
}
