package com.example.final_project.dto.request;

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
