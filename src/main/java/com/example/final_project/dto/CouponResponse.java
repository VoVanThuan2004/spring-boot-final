package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CouponResponse {
    private int id;

    private String code;
    private double discountPrice;
    private int quantity;
    private boolean active;
    private LocalDate expiryDate;
}
