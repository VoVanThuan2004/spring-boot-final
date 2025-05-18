package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ConfirmOTPRequest {
    private String email;
    private String password;
    private String otp;
}
