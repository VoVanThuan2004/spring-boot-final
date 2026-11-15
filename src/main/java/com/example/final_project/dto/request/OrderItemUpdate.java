package com.example.final_project.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class OrderItemUpdate {
    private int orderItemId;
    private int productVariantId;
    private int quantity;
    private double price;
}
