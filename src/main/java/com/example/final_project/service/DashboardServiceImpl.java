package com.example.final_project.service;

import com.example.final_project.dto.*;
import com.example.final_project.entity.Order;
import com.example.final_project.entity.OrderItem;
import com.example.final_project.repository.OrderItemRepository;
import com.example.final_project.repository.OrderRepository;
import com.example.final_project.repository.ProductRepository;
import com.example.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Map<String, Object> getDashboardSummary() {
        int totalUsers = (int) userRepository.count();
        int newUsersThisMonth = totalUsers;  // Cần sửa chỗ nay
        List<Order> orders = orderRepository.findByStatus("Delivered");
        double totalOrders = orders.size();
        double totalRevenue = orderRepository.sumTotalAmountByStatus("Delivered");

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalUsers", totalUsers);
        summary.put("newUsersThisMonth", newUsersThisMonth);
        summary.put("totalOrders", totalOrders);
        summary.put("totalRevenue", totalRevenue);

        return summary;
    }

    @Override
    public List<BestSellingProductDTO> getBestSellingProducts(int limit) {
        List<Object[]> rawData = orderItemRepository.getBestSellingProducts(limit);
        List<BestSellingProductDTO> result = new ArrayList<>();

        for (Object[] row : rawData) {
            String productName = (String) row[0];
            int quantitySold = ((Number) row[1]).intValue();
            double totalRevenue = ((Number) row[2]).doubleValue();

            result.add(new BestSellingProductDTO(productName, quantitySold, totalRevenue));
        }

        return result;
    }

    @Override
    public List<RevenueTrendDTOSimple> getRevenueTrend(int year) {
        // Dữ liệu có doanh thu theo tháng
        List<Map<String, Object>> rawData = orderRepository.getRevenueTrendByYear(year);

        // Đưa dữ liệu vào Map để dễ tra cứu
        Map<Integer, Double> revenueMap = new HashMap<>();
        for (Map<String, Object> row : rawData) {
            int month = ((Number) row.get("month")).intValue();
            double revenue = ((Number) row.get("revenue")).doubleValue();
            revenueMap.put(month, revenue);
        }

        // Tạo danh sách đủ 12 tháng, gán doanh thu 0 nếu không có
        List<Map<String, Object>> fullData = new ArrayList<>();
        List<RevenueTrendDTOSimple> revenueTrendDTOSimples = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
//            Map<String, Object> entry = new HashMap<>();
//            entry.put("month", i);
//            entry.put("revenue", revenueMap.getOrDefault(i, 0.0));
//            fullData.add(entry);
            RevenueTrendDTOSimple revenueTrendDTOSimple = RevenueTrendDTOSimple.builder()
                    .revenue(revenueMap.getOrDefault(i, 0.0))
                    .month(i)
                    .build();
            revenueTrendDTOSimples.add(revenueTrendDTOSimple);
        }

        return revenueTrendDTOSimples;
    }


    @Override
    public List<ProductCategoryStatsDTO> getProductCategoryStats() {
        List<Object[]> rawStats = orderItemRepository.fetchProductCategoryStats();
        List<ProductCategoryStatsDTO> productCategoryStatsDTOList = new ArrayList<>();

        for (Object[] rawStat : rawStats) {
            String categoryName = (String) rawStat[0];
            long quantitySold = ((Number) rawStat[1]).longValue();

            ProductCategoryStatsDTO productCategoryStatsDTO = ProductCategoryStatsDTO.builder()
                    .categoryName(categoryName)
                    .quantitySold(quantitySold)
                    .build();

            productCategoryStatsDTOList.add(productCategoryStatsDTO);
        }

        return productCategoryStatsDTOList;
    }

    @Override
    public List<ProductCategoryStatsDTO> getProductCategoryStatsBetween(LocalDate from, LocalDate to) {
        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(LocalTime.MAX);

        List<Object[]> rawStats = orderItemRepository.fetchProductCategoryStatsBetween(fromTime, toTime);

        List<ProductCategoryStatsDTO> result = new ArrayList<>();
        for (Object[] row : rawStats) {
            String category = (String) row[0];
            long quantity = ((Number) row[1]).longValue();

            result.add(ProductCategoryStatsDTO.builder()
                    .categoryName(category)
                    .quantitySold(quantity)
                    .build());
        }

        return List.of();
    }


    /*
        Advanced Dashboard
    */

    @Override
    public DashboardSummaryDTO getDashboardSummaryByTime(LocalDate from, LocalDate to) {
        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(LocalTime.MAX);

        int totalOrders = orderRepository.countOrdersBetween(fromTime, toTime);

        List<Order> orders = orderRepository.findOrdersBetween(fromTime, toTime);
        double totalRevenue = 0.0;
        double profit = 0.0;
        for (Order order : orders) {
            totalRevenue += order.getTotalAmount();
            for (OrderItem orderItem: order.getOrderItemList()) {
                double price = orderItem.getPrice();
                int quantity = orderItem.getQuantity();
                profit += (price - orderItem.getVariant().getCostPrice()) * quantity;
            }
        }

        return DashboardSummaryDTO.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .totalProfit(profit)
                .build();
    }

    @Override
    public List<RevenueTrendDTO> getRevenueTrendByTime(String interval, LocalDate from, LocalDate to) {
        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findOrdersBetween(fromTime, toTime);

        int totalOrders = orderRepository.countOrdersBetween(fromTime, toTime);

        // Gom nhóm theo thời gian (interval)
        Map<String, List<OrderItem>> groupedItems = orders.stream()
                .flatMap(order -> order.getOrderItemList().stream())
                .collect(Collectors.groupingBy(oi -> {
                    LocalDate date = oi.getOrder().getPurchaseDate().toLocalDate();

                    return switch (interval.toLowerCase()) {
                        case "daily" -> date.toString(); // "2025-05-13"
                        case "weekly" -> date.getYear() + "-W" + date.get(WeekFields.ISO.weekOfWeekBasedYear());
                        case "monthly" -> date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                        case "quarterly" -> date.getYear() + "-Q" + ((date.getMonthValue() - 1) / 3 + 1);
                        case "yearly" -> String.valueOf(date.getYear());
                        default -> "unknown";
                    };
        }));

        // Tính revenue & profit theo từng nhóm
        return groupedItems.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sắp xếp theo thời gian
                .map(entry -> {
                    String label = entry.getKey();
                    List<OrderItem> items = entry.getValue();

                    double revenue = items.stream().mapToDouble(oi -> oi.getPrice() * oi.getQuantity()).sum();
                    double profit = items.stream()
                            .mapToDouble(oi -> (oi.getPrice() - oi.getVariant().getCostPrice()) * oi.getQuantity())
                            .sum();

                    return RevenueTrendDTO.builder()
                            .label(label)
                            .totalRevenue(revenue)
                            .totalProfit(profit)
                            .totalOrders(totalOrders)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCountDTO> getProductCountByTime(String interval, LocalDate from, LocalDate to) {
        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(LocalTime.MAX);

        List<OrderItem> items = orderItemRepository.findByOrderPurchaseDateBetween(fromTime, toTime);

        Map<String, Integer> resultMap = new TreeMap<>(); // đảm bảo thứ tự thời gian

        for (OrderItem item : items) {
            LocalDateTime purchaseDate = item.getOrder().getPurchaseDate();
            String key = formatByInterval(purchaseDate, interval);
            resultMap.put(key, resultMap.getOrDefault(key, 0) + item.getQuantity());
        }

        return resultMap.entrySet().stream()
                .map(e -> new ProductCountDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }


    private String formatByInterval(LocalDateTime dateTime, String interval) {
        switch (interval.toLowerCase()) {
            case "daily":
                return dateTime.toLocalDate().toString(); // yyyy-MM-dd
            case "weekly":
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int week = dateTime.get(weekFields.weekOfWeekBasedYear());
                return dateTime.getYear() + "-W" + week;
            case "monthly":
                return dateTime.getYear() + "-" + String.format("%02d", dateTime.getMonthValue());
            case "quarterly":
                int quarter = (dateTime.getMonthValue() - 1) / 3 + 1;
                return dateTime.getYear() + "-Q" + quarter;
            case "yearly":
                return String.valueOf(dateTime.getYear());
            default:
                throw new IllegalArgumentException("Invalid interval: " + interval);
        }
    }


    @Override
    public List<ComparativeStatDTO> getComparativeStats(String interval, LocalDate from, LocalDate to) {
        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(LocalTime.MAX);

        List<OrderItem> orderItems = orderItemRepository.findByOrderPurchaseDateBetween(fromTime, toTime);

        // Group theo thời gian
        Map<String, List<OrderItem>> grouped = orderItems.stream().collect(Collectors.groupingBy(oi -> {
            LocalDateTime date = oi.getOrder().getPurchaseDate();
            return switch (interval) {
                case "yearly" -> String.valueOf(date.getYear());
                case "quarterly" -> "Q" + ((date.getMonthValue() - 1) / 3 + 1) + "-" + date.getYear();
                case "monthly" -> String.format("%02d/%d", date.getMonthValue(), date.getYear());
                case "weekly" -> "W" + date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + "-" + date.getYear();
                default -> "Unknown";
            };
        }));

        List<ComparativeStatDTO> result = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            String time = entry.getKey();
            List<OrderItem> items = entry.getValue();

            double revenue = items.stream().mapToDouble(oi -> oi.getPrice() * oi.getQuantity()).sum();
            double profit = items.stream().mapToDouble(oi ->
                    (oi.getPrice() - oi.getVariant().getCostPrice()) * oi.getQuantity()).sum();
            int productsSold = (int) items.stream().mapToLong(OrderItem::getQuantity).sum();

            Map<String, Integer> categoryMap = items.stream()
                    .collect(Collectors.groupingBy(oi -> oi.getVariant().getProduct().getCategory().getName(),
                            Collectors.summingInt(OrderItem::getQuantity)));

//            result.add(new ComparativeStatsDTO(time, revenue, profit, productsSold, categoryMap));
            result.add(ComparativeStatDTO.builder()
                            .time(time)
                            .revenue(revenue)
                            .profit(profit)
                            .productsSold(productsSold)
                            .categories(categoryMap)
                    .build());
        }

        // Sort theo thời gian nếu cần
        result.sort(Comparator.comparing(ComparativeStatDTO::getTime));

        return result;
    }
}
