package com.example.final_project.dto;

import com.example.final_project.entity.ProductVariant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class OrderItemResponse {
    private int orderItemId;
    private String productVariantName;
    private String image;

    private int quantity;
    private double price;
}
