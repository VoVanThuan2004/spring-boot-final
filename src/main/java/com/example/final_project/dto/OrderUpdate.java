package com.example.final_project.dto;

import com.example.final_project.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
public class OrderUpdate {
    private int userId;

    private LocalDateTime purchaseDate;
    private double totalAmount;

    private String couponCode;      // Lưu mã giảm gia

    private String status;
    private List<OrderItemUpdate> orderItemUpdates;
}
