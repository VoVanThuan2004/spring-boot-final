package com.example.final_project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class BrandRequest {
    @NotBlank(message = "Brand name is required")
    private String name;
}
