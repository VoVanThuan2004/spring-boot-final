package com.example.final_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
public class BestSellingProductDTO {
    private String productName;
    private int quantity;
    private double totalRevenue;
}
