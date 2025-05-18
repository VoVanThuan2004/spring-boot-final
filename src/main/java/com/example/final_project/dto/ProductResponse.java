package com.example.final_project.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class ProductResponse {
    private int id;
    private String name;
    private double price;
    private String image;
    private String description;

    // Lưu thông số product (RAM, ROM,...)
    private String specs;

    private LocalDateTime createdAt;

    private String categoryName;
    private String brandName;

    // Phần trăm giảm giá
    private double discountPercent;
}
