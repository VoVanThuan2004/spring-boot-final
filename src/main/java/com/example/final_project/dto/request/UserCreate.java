package com.example.final_project.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserCreate {
    private String fullName;
    private String email;
    private String password;

    private AddressRequest address;
}
