package com.example.final_project.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class LoginResponse {
    private int userId;
    private String fullName;
    private String email;
    private String role;
    private String accessToken;
}
