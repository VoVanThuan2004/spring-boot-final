package com.example.final_project.controller;

import com.example.final_project.dto.CustomUserDetails;
import com.example.final_project.dto.OrderRequest;
import com.example.final_project.dto.OrderResponse;
import com.example.final_project.dto.OrderUpdate;
import com.example.final_project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5000")
public class OrderController {
    private final OrderService orderService;

    // Get list đơn hàng, kết hợp phân trang
    @GetMapping("/api/v1/orders")
    public Page<OrderResponse> getAllOrders(
            @RequestParam int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.getAllOrders(pageable);
    }

    // Get list don hang, theo thoi gian
    @GetMapping("/api/v1/orders/timeline")
    public Page<OrderResponse> getOrderTimeline(
            @RequestParam int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.getOrderTimeline(pageable, filter, startDate, endDate);
    }

    // Update order
    @PutMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<String> updateOrder(
            @PathVariable int orderId,
            @RequestBody OrderUpdate orderUpdate
    ) {
        return orderService.updateOrder(orderId, orderUpdate);
    }

    // Thanh toan
    @PostMapping("/api/v1/orders")
    public ResponseEntity<?> createOrder(
            @RequestBody(required = false) OrderRequest orderRequest,
            Principal principal
            ) {
        return orderService.createOrder(orderRequest, principal);
    }

    // Tracking status của order
    @PutMapping("/api/v1/orders/tracking/{orderId}")
    public ResponseEntity<?> trackOrder(
            @PathVariable int orderId,
            @RequestParam String newStatus
    ) {
        return orderService.trackOrder(orderId, newStatus);
    }

    // Xem list status của order
    @GetMapping("/api/v1/orders/status/{orderId}")
    public ResponseEntity<?> getOrderStatus(@PathVariable int orderId) {
        return orderService.getOrderStatus(orderId);
    }


    /*
        Xem lich su don hang
    */
    @GetMapping("/api/v1/orders/history")
    public List<OrderResponse> getOrderHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getId();
        return orderService.getOrderHistory(userId);
    }

}
