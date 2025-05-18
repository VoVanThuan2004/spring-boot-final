package com.example.final_project.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class CategoryResponse {
    private int id;
    private String name;
}
