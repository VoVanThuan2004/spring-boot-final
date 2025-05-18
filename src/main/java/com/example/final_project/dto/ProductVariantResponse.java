package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
public class ProductVariantResponse {
    private int id;

    private String variantName;
    private double costPrice;   // Giá gốc
    private double priceDiff;   // Gia chenh lech
    private String specs;       // Lưu thông số product json (RAM, ROM,...)

    private LocalDateTime createdAt;

    // Phần trăm giảm giá
    private double discountPercent;

    // Lưu inventory
    private int quantity;
    // Lưu images
    private List<String> images;
}
