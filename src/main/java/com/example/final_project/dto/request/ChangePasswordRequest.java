package com.example.final_project.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
