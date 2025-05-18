package com.example.final_project.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class UserLogin {
    private String email;
    private String password;
}
