package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class TokenPayload {
    private int userId;
    private String fullName;
    private String role;
//    private String email;
}
