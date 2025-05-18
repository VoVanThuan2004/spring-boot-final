package com.example.final_project.security;

import com.example.final_project.dto.TokenPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/register/user/").permitAll()
                        .requestMatchers("/api/v1/login").permitAll()
                        .requestMatchers("/api/v1/recovery/password").permitAll()
                        .requestMatchers("/api/v1/reset/password").permitAll()
                        .requestMatchers("/api/v1/users").permitAll()
                        .requestMatchers("/api/v1/users/**").permitAll()
                        .requestMatchers("/api/v2/users/**").permitAll()
                        .requestMatchers("/api/v1/profile/**").permitAll()
                        .requestMatchers("/api/v1/products/**").permitAll()
                        .requestMatchers("/api/v1/productVariants").permitAll()
                        .requestMatchers("/api/v1/productVariants/**").permitAll()
                        .requestMatchers("/api/v1/update/quantity").permitAll()
                        .requestMatchers("/api/v1/orders").permitAll()
                        .requestMatchers("api/v1/orders/**").permitAll()
                        .requestMatchers("/api/v1/orders/timeline").permitAll()
                        .requestMatchers("/api/v1/orders/status/**").permitAll()
                        .requestMatchers("/api/v1/orders/tracking/**").permitAll()
                        .requestMatchers("/api/v1/coupon**").permitAll()
                        .requestMatchers("/api/v1/coupon/**").permitAll()
                        .requestMatchers("/api/v1/category/**").permitAll()
                        .requestMatchers("/api/v1/brand/**").permitAll()
                        .requestMatchers("/api/v1/cart/**").permitAll()
                        .requestMatchers("/api/v1/user/").authenticated()
                        .requestMatchers("/api/v1/reviews/**").permitAll()
                        .requestMatchers("/api/v1/dashboard/summary").permitAll()              
                        .requestMatchers("/api/v1/dashboard/**").permitAll()
                        .requestMatchers("/api/v1/change-password/user").authenticated()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/v1/ratings").authenticated()
                        .requestMatchers("/api/v1/ratings/**").permitAll()
                        .requestMatchers("/api/v1/message/send").authenticated()
                        .anyRequest().authenticated()

                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Thêm cấu hình CORS

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    // Thêm cấu hình CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://flutter-35eh.onrender.com")); // Origin của client
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Nếu cần gửi credentials (JWT qua header)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
