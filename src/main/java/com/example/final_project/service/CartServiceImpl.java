package com.example.final_project.service;

import com.example.final_project.dto.*;
import com.example.final_project.entity.*;
import com.example.final_project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;

    @Override
    public ResponseEntity<String> addToCart(AddToCartRequest request, Principal principal) {

        Optional<Cart> cart;

        // tao gio hang
        if (principal != null) {
            // User đã đăng nhập → lấy từ authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            int userId = userDetails.getId();

            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            cart = cartRepository.findByUserId(userId);
            if (cart.isEmpty()) {
                Cart newCart = new Cart();
                newCart.setUser(user.get());
                cartRepository.save(newCart);
                cart = Optional.of(newCart);
            }

        }

        else {
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                return ResponseEntity.badRequest().body("Session ID is required for guest user.");
            }

            cart = cartRepository.findBySessionId(sessionId);
            if (cart.isEmpty()) {
                Cart newCart = new Cart();
                newCart.setSessionId(sessionId);
                cartRepository.save(newCart);
                cart = Optional.of(newCart);
            }
        }

        // Xử lý thêm sản phẩm vào giỏ hàng
        Optional<ProductVariant> productVariant = productVariantRepository.findById(request.getProductVariantId());
        if (productVariant.isEmpty()) {
            return ResponseEntity.badRequest().body("Product variant not found");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndVariantId(cart.get().getId(), productVariant.get().getId());

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
        }
        else {
            CartItem newCartItem = new CartItem();
            newCartItem.setQuantity(request.getQuantity());
            newCartItem.setVariant(productVariant.get());
            newCartItem.setPrice(productVariant.get().getPriceDiff());
            newCartItem.setCart(cart.get());
            cartItemRepository.save(newCartItem);
        }


        return ResponseEntity.ok("Added to cart successfully.");
    }

    @Override
    public ResponseEntity<?> getCart(Principal principal, String sessionId) {
        Optional<Cart> cart;

        if (principal != null) {
            // User đã đăng nhập → lấy từ authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            int userId = userDetails.getId();

            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            cart = cartRepository.findByUserId(userId);
            if (cart.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart is empty");
            }
        }
        else {
            if (sessionId == null || sessionId.isEmpty()) {
                return ResponseEntity.badRequest().body("Session ID is required for guest user.");
            }

            cart = cartRepository.findBySessionId(sessionId);
            if (cart.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart is empty");
            }
        }

        List<CartItem> cartItemList = cartItemRepository.findByCartId(cart.get().getId());

        List<CartItemResponse> cartItemResponseList = new ArrayList<>();

        double totalPrice = 0;
        for (CartItem cartItem: cartItemList) {
            CartItemResponse cartItemResponse = CartItemResponse.builder()
                    .cartItemId(cartItem.getId())
                    .productVariantId(cartItem.getVariant().getId())
                    .productName(cartItem.getVariant().getVariantName())
                    .image(cartItem.getVariant().getImages().get(0).getImagePath())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();

            totalPrice += cartItem.getPrice() * cartItem.getQuantity();
            cartItemResponseList.add(cartItemResponse);
        }

        // Kiem tra ma coupon
        Coupon applyCoupon = cart.get().getCoupon();
        CouponResponse couponResponse = null;

        if (applyCoupon != null) {
            if (applyCoupon.isActive() && applyCoupon.getQuantity() >= 1 && applyCoupon.getDiscountPrice() <= totalPrice) {
                totalPrice = totalPrice - applyCoupon.getDiscountPrice();
                couponResponse = CouponResponse.builder()
                        .id(applyCoupon.getId())
                        .active(applyCoupon.isActive())
                        .code(applyCoupon.getCode())
                        .discountPrice(applyCoupon.getDiscountPrice())
                        .quantity(applyCoupon.getQuantity())
                        .build();
            }
        }

        CartResponse cartResponse = CartResponse.builder()
                .id(cart.get().getId())
                .userId(cart.get().getUser() != null ? String.valueOf(cart.get().getUser().getId()) : null)
                .sessionId(sessionId)
                .cartItemResponseList(cartItemResponseList)
                .totalPrice(totalPrice)
                .couponResponse(couponResponse)
                .build();

        return ResponseEntity.ok(cartResponse);
    }

    @Override
    public ResponseEntity<String> deleteCartItem(int cartItemId, Principal principal, String sessionId) {
        if (principal != null) {
            // User đã đăng nhập → lấy từ authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            int userId = userDetails.getId();

            Optional<Cart> cart = cartRepository.findByUserId(userId);
            if (cart.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart is empty");
            }

            Optional<CartItem> cartItem = cartItemRepository.findByCartIdAndId(cart.get().getId(), cartItemId);
            if (cartItem.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart item not found");
            }

            // Xoa cartItem
            cartItemRepository.delete(cartItem.get());
            return ResponseEntity.ok("CartItem deleted successfully");
        }

        else if (sessionId != null || !sessionId.isEmpty()) {
            Optional<Cart> cart = cartRepository.findBySessionId(sessionId);
            if (cart.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart is empty with sessionID");
            }

            Optional<CartItem> cartItem = cartItemRepository.findByCartIdAndId(cart.get().getId(), cartItemId);
            if (cartItem.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart item not found");
            }

            // Xoa cartItem
            cartItemRepository.delete(cartItem.get());
            return ResponseEntity.ok("CartItem deleted successfully");
        }

        else {
            // Nếu không có cả principal và sessionId, trả về lỗi
            return ResponseEntity.badRequest().body("Session ID or user authentication is required");
        }
    }

    @Override
    public ResponseEntity<String> updateCartItemQuantity(int cartItemId, Map<String, Integer> body, Principal principal, String sessionId) {
        int newQuantity = body.get("quantity");
        if (newQuantity <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be greater than zero");
        }

        Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);
        if (cartItem.isEmpty()) {
            return ResponseEntity.badRequest().body("CartItem not found");
        }

        Cart cart = cartItem.get().getCart();

        // Kiểm tra quyền truy cập của người dùng (xác thực user hoặc session)
        if (principal != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (cart.getUser() == null || cart.getUser().getId() != userDetails.getId()) {
                return ResponseEntity.status(403).body("Access denied");
            }
        } else {
            if (sessionId == null || !sessionId.equals(cart.getSessionId())) {
                return ResponseEntity.status(403).body("Session ID mismatch");
            }
        }

        cartItem.get().setQuantity(newQuantity);
        cartItemRepository.save(cartItem.get());
        return ResponseEntity.ok("Quantity updated successfully");
    }

    @Override
    public ResponseEntity<?> applyCouponAndLoyaltyPoint(String couponCode, int loyaltyPoint, Principal principal, String sessionId) {
        Optional<Cart> cart;

        if (principal != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            int userId = userDetails.getId();

            cart = cartRepository.findByUserId(userId);
        }
        else {
            if (sessionId == null) {
                return ResponseEntity.badRequest().body("Session ID is required");
            }
            cart = cartRepository.findBySessionId(sessionId);
        }

        if (cart.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is not found");
        }

        // Lấy danh sách các món hàng trong giỏ hàng
        List<CartItem> cartItemList = cartItemRepository.findByCartId(cart.get().getId());
        double totalPrice = cartItemList.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Check mã coupon
        double discountCoupon = 0;
        double discountLoyaltyPoint = 0;
        if (couponCode != null && !couponCode.isEmpty()) {
            Optional<Coupon> coupon = couponRepository.findByCode(couponCode);
            if (coupon.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid coupon code");
            }

            if (!coupon.get().isActive() || coupon.get().getQuantity() <= 0) {
                return ResponseEntity.badRequest().body("Coupon code is expired or has no remaining usage");
            }


            discountCoupon = coupon.get().getDiscountPrice();

            if (coupon.get().getDiscountPrice() > totalPrice) {
                return ResponseEntity.badRequest().body("Coupon discount exceeds total price");
            }

            cart.get().setCoupon(coupon.get());

        }

        // Áp dụng điểm loyalty nếu có
        if (loyaltyPoint > 0 && cart.get().getUser() != null) {
            Optional<LoyaltyPoint> userPoint = loyaltyPointRepository.findByUserId(cart.get().getUser().getId());
            if (userPoint.isEmpty() || userPoint.get().getPoints() < loyaltyPoint) {
                return ResponseEntity.badRequest().body("Not enough loyalty points");
            }

            discountLoyaltyPoint = loyaltyPoint;
            cart.get().setLoyaltyPoint(loyaltyPoint);
        }

        // Tinh tong tien
        double finalPrice = totalPrice - discountCoupon - discountLoyaltyPoint;


        // Chuyển đồi kiểu dữ liệu trả về
        List<CartItemResponse> cartItemResponseList = cartItemList.stream()
                .map(cartItem -> CartItemResponse.builder()
                        .productVariantId(cartItem.getVariant().getId())
                        .productName(cartItem.getVariant().getVariantName())
                        .price(cartItem.getPrice())
                        .quantity(cartItem.getQuantity())
                        .image(cartItem.getVariant().getImages().get(0).getImagePath())
                        .build())
                .toList();

        CartResponse cartResponse = CartResponse.builder()
                .id(cart.get().getId())
                .sessionId(sessionId)
                .userId(cart.get().getUser() != null ? String.valueOf(cart.get().getUser().getId()) : null)
                .cartItemResponseList(cartItemResponseList)
                .totalPrice(finalPrice)
                .build();


        cartRepository.save(cart.get());

        return ResponseEntity.ok(cartResponse);
    }

    @Override
    public ResponseEntity<?> removeCoupon(Principal principal, String sessionId) {
        Optional<Cart> cart;

        if (principal != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            int userId = userDetails.getId();

            cart = cartRepository.findByUserId(userId);
        }
        else {
            if (sessionId == null) {
                return ResponseEntity.badRequest().body("Session ID is required");
            }
            cart = cartRepository.findBySessionId(sessionId);
        }

        if (cart.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is not found");
        }

        cart.get().setCoupon(null);
        cart.get().setLoyaltyPoint(0);
        cartRepository.save(cart.get());

        return ResponseEntity.ok("Coupon removed successfully");
    }

}
