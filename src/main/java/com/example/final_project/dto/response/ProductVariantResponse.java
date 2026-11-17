package com.example.final_project.dto.response;

import com.example.final_project.enums.VariantStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
public class ProductVariantResponse {
    private int id;

    private String sku;
    private String variantName;
    private String name;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private BigDecimal costPrice;
    private double priceDiff;
    private String specs;
    private VariantStatus status;

    private LocalDateTime createdAt;

    private double discountPercent;

    private int quantity;
    private int reservedQuantity;
    private int availableQuantity;
    private List<String> images;
}
