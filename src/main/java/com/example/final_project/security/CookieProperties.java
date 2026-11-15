package com.example.final_project.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "app.cookie")
@Getter @Setter
public class CookieProperties {
    private static final Set<String> SUPPORTED_SAME_SITE = Set.of("lax", "strict", "none");

    private String refreshTokenName = "refresh_token";
    private String path = "/api/v1/auth";
    private String sameSite = "Lax";
    private boolean secure = false;
    private String domain = "";

    @PostConstruct
    public void validate() {
        if (sameSite == null || !SUPPORTED_SAME_SITE.contains(sameSite.toLowerCase(Locale.ROOT))) {
            throw new IllegalStateException("app.cookie.same-site must be one of: Lax, Strict, None");
        }
        if ("none".equalsIgnoreCase(sameSite) && !secure) {
            throw new IllegalStateException("app.cookie.secure must be true when same-site is None");
        }
    }

    public boolean hasDomain() {
        return domain != null && !domain.isBlank();
    }
}
