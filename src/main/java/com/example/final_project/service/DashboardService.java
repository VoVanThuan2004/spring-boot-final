package com.example.final_project.service;

import com.example.final_project.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardSummary();

    List<BestSellingProductDTO> getBestSellingProducts(int limit);

    List<RevenueTrendDTOSimple> getRevenueTrend(int year);

    List<ProductCategoryStatsDTO> getProductCategoryStats();

    DashboardSummaryDTO getDashboardSummaryByTime(LocalDate from, LocalDate to);

    List<RevenueTrendDTO> getRevenueTrendByTime(String interval, LocalDate from, LocalDate to);

    List<ProductCountDTO> getProductCountByTime(String interval, LocalDate from, LocalDate to);

    List<ProductCategoryStatsDTO> getProductCategoryStatsBetween(LocalDate from, LocalDate to);

    List<ComparativeStatDTO> getComparativeStats(String interval, LocalDate from, LocalDate to);
}
