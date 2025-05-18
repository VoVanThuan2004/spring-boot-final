package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class DashboardSummaryDTO {
    private int totalOrders;
    private double totalRevenue;
    private double totalProfit;
}
