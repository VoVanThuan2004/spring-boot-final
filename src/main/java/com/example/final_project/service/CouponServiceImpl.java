package com.example.final_project.service;

import com.example.final_project.dto.CouponRequest;
import com.example.final_project.dto.CouponResponse;
import com.example.final_project.entity.Coupon;
import com.example.final_project.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    @Override
    public List<CouponResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        List<CouponResponse> couponResponses = new ArrayList<>();
        for (Coupon coupon : coupons) {
            CouponResponse couponResponse = CouponResponse.builder()
                    .id(coupon.getId())
                    .code(coupon.getCode())
                    .discountPrice(coupon.getDiscountPrice())
                    .quantity(coupon.getQuantity())
                    .active(coupon.isActive())
                    .expiryDate(coupon.getExpiryDate())
                    .build();
            couponResponses.add(couponResponse);
        }
        return couponResponses;
    }

    @Override
    public ResponseEntity<String> createCoupon(CouponRequest couponRequest) {
        if (couponRequest == null) {
            return ResponseEntity.badRequest().body("Invalid coupon request");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(couponRequest.getCode());
        coupon.setDiscountPrice(couponRequest.getDiscountPrice());
        coupon.setQuantity(couponRequest.getQuantity());
        coupon.setActive(couponRequest.isActive());
        coupon.setExpiryDate(couponRequest.getExpiryDate());
        couponRepository.save(coupon);
        return ResponseEntity.ok("Coupon Created");
    }

    @Override
    public ResponseEntity<String> deleteCoupon(int couponId) {
        Optional<Coupon> coupon = couponRepository.findById(couponId);
        if (coupon.isEmpty()) {
            return ResponseEntity.badRequest().body("Coupon does not found");
        }

        couponRepository.delete(coupon.get());
        return ResponseEntity.ok("Coupon Deleted");
    }

    @Override
    public ResponseEntity<String> updateCoupon(int couponId, CouponRequest couponRequest) {
        Optional<Coupon> coupon = couponRepository.findById(couponId);
        if (coupon.isEmpty()) {
            return ResponseEntity.badRequest().body("Coupon does not found");
        }

        coupon.get().setCode(couponRequest.getCode());
        coupon.get().setDiscountPrice(couponRequest.getDiscountPrice());
        coupon.get().setQuantity(couponRequest.getQuantity());
        coupon.get().setActive(couponRequest.isActive());
        coupon.get().setExpiryDate(couponRequest.getExpiryDate());
        couponRepository.save(coupon.get());
        return ResponseEntity.ok("Coupon Updated");
    }

    @Override
    public List<CouponResponse> getAllCouponsForUsers() {
        List<Coupon> coupons = couponRepository.findAll();
        List<CouponResponse> couponResponses = new ArrayList<>();
        for (Coupon coupon : coupons) {
            if (coupon.isActive()) {
                CouponResponse couponResponse = CouponResponse.builder()
                        .id(coupon.getId())
                        .code(coupon.getCode())
                        .discountPrice(coupon.getDiscountPrice())
                        .quantity(coupon.getQuantity())
                        .active(coupon.isActive())
                        .expiryDate(coupon.getExpiryDate())
                        .build();
                couponResponses.add(couponResponse);
            }
        }
        return couponResponses;
    }
}
