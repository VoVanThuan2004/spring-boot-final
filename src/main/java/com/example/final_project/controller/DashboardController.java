package com.example.final_project.controller;

import com.example.final_project.dto.*;
import com.example.final_project.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5000")
public class DashboardController {
    private final DashboardService dashboardService;
    /*
        Admin Dashboard
     */
    @GetMapping("/api/v1/dashboard/summary")
    public Map<String, Object> getDashboardSummary() {
        return dashboardService.getDashboardSummary();
    }

    // Get list product bán chạy
    @GetMapping("/api/v1/dashboard/best-selling")
    public List<BestSellingProductDTO> getBestSellingProducts(@RequestParam(defaultValue = "5") int limit) {
        return dashboardService.getBestSellingProducts(limit);
    }

    // Get doanh thu theo tháng trong năm
    @GetMapping("/api/v1/dashboard/revenue-trend")
    public List<RevenueTrendDTOSimple> getRevenueTrend(@RequestParam(defaultValue = "2025") int year) {
        return dashboardService.getRevenueTrend(year);
    }

    // Get ra các doanh mục (category) + số lượng bán
    @GetMapping("/api/v1/dashboard/product-category-stats")
    public List<ProductCategoryStatsDTO> getProductCategoryStats(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        if (from != null && to != null) {
            return dashboardService.getProductCategoryStatsBetween(from, to);
        }
        return dashboardService.getProductCategoryStats();
    }


    /*
        Advanced Dashboard
     */
    // Tổng số đơn hang, doanh thu, lợi nhuận trong khoảng thời gian
    @GetMapping("/api/v1/dashboard/summary/")
    public DashboardSummaryDTO getDashboardSummaryByTime(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
            ) {
        return dashboardService.getDashboardSummaryByTime(from, to);
    }

    // Tong don hang, Doanh thu, lợi nhuận theo thời gian
    @GetMapping("/api/v1/dashboard/revenue-trend/")
    public List<RevenueTrendDTO> getRevenueTrendByTime(
            @RequestParam(defaultValue = "yearly") String interval,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return dashboardService.getRevenueTrendByTime(interval, from, to);
    }

    // Thống kê số lượng sản phẩm đã bán theo khoảng thời gian và loại khoảng thời gian
    @GetMapping("/api/v1/dashboard/product-count")
    public List<ProductCountDTO> getProductCountByTime(
            @RequestParam String interval,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ){
        return dashboardService.getProductCountByTime(interval, from, to);
    }

    // So sánh dữ liệu giữa các interval
    @GetMapping("/api/v1/dashboard/comparative")
    public List<ComparativeStatDTO> getComparativeStats(
            @RequestParam(defaultValue = "yearly") String interval,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return dashboardService.getComparativeStats(interval, from, to);
    }


}
