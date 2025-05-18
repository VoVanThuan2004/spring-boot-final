package com.example.final_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RevenueTrendDTO {
    private String label;
    private double totalRevenue;
    private double totalProfit;
    private int totalOrders;    // so luong order
}
