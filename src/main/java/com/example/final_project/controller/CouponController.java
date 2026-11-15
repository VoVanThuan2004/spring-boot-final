package com.example.final_project.controller;

import com.example.final_project.dto.request.CouponRequest;
import com.example.final_project.dto.response.CouponResponse;
import com.example.final_project.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @GetMapping("/api/v1/coupon")
    public List<CouponResponse> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/api/v1/coupon/users")
    public List<CouponResponse> getAllCouponsForUsers() {
        return couponService.getAllCouponsForUsers();
    }

    @PostMapping("/api/v1/coupon")
    public ResponseEntity<String> createCoupon(@RequestBody CouponRequest couponRequest) {
        return couponService.createCoupon(couponRequest);
    }

    @DeleteMapping("/api/v1/coupon/{couponId}")
    public ResponseEntity<String> deleteCoupon(@PathVariable int couponId) {
        return couponService.deleteCoupon(couponId);
    }

    @PutMapping("/api/v1/coupon/{couponId}")
    public ResponseEntity<String> updateCoupon(@PathVariable int couponId, @RequestBody CouponRequest couponRequest) {
        return couponService.updateCoupon(couponId, couponRequest);
    }

}
