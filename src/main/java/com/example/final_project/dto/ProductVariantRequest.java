package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class ProductVariantRequest {
    private String variantName;
    private double costPrice;   // Giá gốc
    private double priceDiff;   // Gia chenh lech
    private String specs;       // Thông số (RAM, ROM, ...) json
    private int productId;
    private int quantity;
}
