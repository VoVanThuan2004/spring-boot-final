package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class CartResponse {
    private int id;

    private String sessionId;

    private String userId;

    private List<CartItemResponse> cartItemResponseList;
    private double totalPrice;

    private CouponResponse couponResponse;
}
