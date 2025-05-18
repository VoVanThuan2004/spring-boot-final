package com.example.final_project.service;

import com.example.final_project.dto.OrderRequest;
import com.example.final_project.dto.OrderResponse;
import com.example.final_project.dto.OrderUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    ResponseEntity<?> createOrder(OrderRequest orderRequest, Principal principal);

    ResponseEntity<?> trackOrder(int orderId, String newStatus);

    ResponseEntity<?> getOrderStatus(int orderId);

    Page<OrderResponse> getAllOrders(Pageable pageable);

    Page<OrderResponse> getOrderTimeline(Pageable pageable, String filter, LocalDate starDate, LocalDate endDate);

    ResponseEntity<String> updateOrder(int orderId, OrderUpdate orderUpdate);

    List<OrderResponse> getOrderHistory(int userId);
}
