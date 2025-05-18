package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponse {
    private int orderId;
    private String fullName;
    private LocalDateTime purchaseDate;

    private double totalAmount;

    private String couponCode;
    private String status;

    // Địa chỉ giao hàng
    private AddressResponse address;
    private List<OrderItemResponse> orderItems;
}
