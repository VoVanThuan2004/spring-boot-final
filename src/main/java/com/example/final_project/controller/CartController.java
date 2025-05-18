package com.example.final_project.controller;

import com.example.final_project.dto.AddToCartRequest;
import com.example.final_project.dto.CartResponse;
import com.example.final_project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/api/v1/cart/add")
    public ResponseEntity<String> addCart(@RequestBody AddToCartRequest request, Principal principal) {
        return cartService.addToCart(request, principal);
    }

    @GetMapping("/api/v1/cart")
    public ResponseEntity<?> getCart(Principal principal, @RequestParam(required = false) String sessionId) {
        return cartService.getCart(principal, sessionId);
    }

    @DeleteMapping("/api/v1/cart/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable int cartItemId, Principal principal, @RequestParam(required = false) String sessionId) {
        return cartService.deleteCartItem(cartItemId, principal, sessionId);
    }

    @PutMapping("/api/v1/cart/{cartItemId}")
    public ResponseEntity<String> updateCartItemQuantity(
            @PathVariable int cartItemId,
            @RequestBody Map<String, Integer> body,
            Principal principal,
            @RequestParam(required = false) String sessionId
    ) {
        return cartService.updateCartItemQuantity(cartItemId, body, principal, sessionId);
    }

    @PostMapping("/api/v1/cart/apply-coupon")
    public ResponseEntity<?> applyCouponAndLoyaltyPoint(
            @RequestParam String couponCode,
            @RequestParam(defaultValue = "0") int loyaltyPoint,
            Principal principal,
            @RequestParam(required = false) String sessionId
    ) {
        return cartService.applyCouponAndLoyaltyPoint(couponCode, loyaltyPoint, principal, sessionId);
    }

    @DeleteMapping("/api/v1/cart/remove-coupon")
    public ResponseEntity<?> removeCoupon(
            Principal principal,
            @RequestParam(required = false) String sessionId
    ) {
        return cartService.removeCoupon(principal, sessionId);
    }

}
