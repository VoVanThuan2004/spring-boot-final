package com.example.final_project.service;

import com.example.final_project.dto.AddToCartRequest;
import com.example.final_project.dto.CartResponse;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Map;

public interface CartService {
    ResponseEntity<String> addToCart(AddToCartRequest request, Principal principal);

    ResponseEntity<?> getCart(Principal principal, String sessionId);

    ResponseEntity<String> deleteCartItem(int cartItemId, Principal principal, String sessionId);

    ResponseEntity<String> updateCartItemQuantity(int cartItemId, Map<String, Integer> body, Principal principal, String sessionId);

    ResponseEntity<?> applyCouponAndLoyaltyPoint(String couponCode, int loyaltyPoint, Principal principal, String sessionId);

    ResponseEntity<?> removeCoupon(Principal principal, String sessionId);

}
