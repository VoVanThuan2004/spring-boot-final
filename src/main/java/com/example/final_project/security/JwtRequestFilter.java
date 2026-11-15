package com.example.final_project.security;

import com.example.final_project.dto.CustomUserDetails;
import com.example.final_project.dto.TokenPayload;
import com.example.final_project.entity.Role;
import com.example.final_project.entity.User;
import com.example.final_project.entity.UserRole;
import com.example.final_project.repository.UserRepository;
import com.example.final_project.repository.UserRoleRepository;
import com.example.final_project.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null
                && authorizationHeader.startsWith(BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            try {
                TokenPayload payload = jwtUtil.validateAccessToken(token);
                authenticate(payload);
            } catch (JwtException | IllegalArgumentException ex) {
                log.debug("Rejected JWT: {}", ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(TokenPayload payload) {
        Optional<User> userOptional = userRepository.findById(payload.getUserId());
        if (userOptional.isEmpty()) {
            log.debug("JWT references an unknown user: {}", payload.getUserId());
            return;
        }

        User user = userOptional.get();
        if (!user.isActive()) {
            log.debug("JWT references a locked user: {}", user.getId());
            return;
        }

        Optional<UserRole> userRoleOptional = userRoleRepository.findByUserId(user.getId());
        if (userRoleOptional.isEmpty() || userRoleOptional.get().getRole() == null) {
            log.debug("JWT references a user without a role: {}", user.getId());
            return;
        }

        Role role = userRoleOptional.get().getRole();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.getName()));
        UserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getFullName(),
                user.getPassword(),
                user.getEmail(),
                authorities
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
