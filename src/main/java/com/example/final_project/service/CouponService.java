package com.example.final_project.service;

import com.example.final_project.dto.CouponRequest;
import com.example.final_project.dto.CouponResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CouponService {
    List<CouponResponse> getAllCoupons();

    ResponseEntity<String> createCoupon(CouponRequest couponRequest);

    ResponseEntity<String> deleteCoupon(int couponId);

    ResponseEntity<String> updateCoupon(int couponId, CouponRequest couponRequest);

    List<CouponResponse> getAllCouponsForUsers();
}
