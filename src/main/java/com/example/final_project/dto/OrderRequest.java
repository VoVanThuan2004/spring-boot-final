package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class OrderRequest {
    private String fullName;
    private String email;

    private AddressDTO address;
    private String sessionId;
}
