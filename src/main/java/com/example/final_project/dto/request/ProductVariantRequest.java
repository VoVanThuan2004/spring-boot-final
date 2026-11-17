package com.example.final_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class ProductVariantRequest {
    private String sku;
    private String variantName;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private BigDecimal costPrice;
    private double priceDiff;
    private String specs;
    private int productId;
    private int quantity;
}
