package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class UserResponse {
    private int userId;
    private String fullName;
    private String email;
    private String image;
    private boolean active;
    private List<AddressResponse> addresses;
}
