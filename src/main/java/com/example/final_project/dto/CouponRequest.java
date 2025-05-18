package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
@Builder
public class CouponRequest {
    private String code;
    private double discountPrice;
    private int quantity;
    private boolean active;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expiryDate;   // yyyy-mm-dd
}
