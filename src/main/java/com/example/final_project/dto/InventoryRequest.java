package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class InventoryRequest {
    private int productId;
    private int quantity;
}
