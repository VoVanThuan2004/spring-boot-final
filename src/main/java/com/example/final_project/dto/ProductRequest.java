package com.example.final_project.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class ProductRequest {
    private String name;
    private double price;
    private String description;

    // Lưu thông số product json (RAM, ROM,...)
    private String specs;

    private int category_id;
    private int brand_id;
}
