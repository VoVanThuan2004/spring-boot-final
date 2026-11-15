package com.example.final_project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;
}
