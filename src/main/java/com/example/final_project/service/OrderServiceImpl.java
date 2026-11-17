package com.example.final_project.service;

import com.example.final_project.dto.request.*;
import com.example.final_project.dto.response.*;
import com.example.final_project.dto.CustomUserDetails;
import com.example.final_project.entity.*;
import com.example.final_project.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;
    private final EmailService emailService;
    private final CouponRepository couponRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AddressRepository addressRepository;
    private final ProductImageResolver productImageResolver;


    @Override
    public ResponseEntity<?> createOrder(OrderRequest orderRequest, Principal principal) {
        // 1. Xác định giỏ hàng (user hoặc session)
        Optional<Cart> cart;
        User user;

        if (principal != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            user = userRepository.findById(userDetails.getId()).orElseThrow();
            cart = cartRepository.findByUserId(user.getId());
        }
        else {
            if (orderRequest.getSessionId() == null) {
                return ResponseEntity.badRequest().body("Session ID required");
            }

            cart = cartRepository.findBySessionId(orderRequest.getSessionId());
            if (cart.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart not found");
            }

            // check khi user nhâp email để tạo tài khoản có exist không
            Optional<User> existingUser = userRepository.findByEmail(orderRequest.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("Email already exists");
            }
            else {
                // Tạo tài khoản mới
                String passwordPlain = RandomStringUtils.randomAlphanumeric(8);
                user = new User();
                user.setEmail(orderRequest.getEmail());
                user.setFullName(orderRequest.getFullName());
                user.setPassword(passwordPlain);
                user = userRepository.save(user);

                // Nhập địa chỉ giao hàng
                Address address = new Address();
                address.setWard(orderRequest.getAddress().getWard());
                address.setWardCode(orderRequest.getAddress().getWardCode());
                address.setDistrict(orderRequest.getAddress().getDistrict());
                address.setDistrictCode(orderRequest.getAddress().getDistrictCode());
                address.setProvince(orderRequest.getAddress().getProvince());
                address.setProvinceCode(orderRequest.getAddress().getProvinceCode());
                address.setAddressDetail(orderRequest.getAddress().getAddressDetail());
                address.setDefault(true);
                address.setUser(user);

                addressRepository.save(address);


                // Role
                Role role = roleRepository.findByName("USER").orElseThrow();
                UserRole userRole = new UserRole();
                userRole.setRole(role);
                userRole.setUser(user);
                userRoleRepository.save(userRole);

                LoyaltyPoint loyaltyPointDefault = new LoyaltyPoint();
                loyaltyPointDefault.setUser(user);
                loyaltyPointDefault.setPoints(0);
                loyaltyPointDefault.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

                // Gửi email
                emailService.sendAccountCreated(user.getEmail(), passwordPlain);
            }


        }

        if (cart.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart not found");
        }

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.get().getId());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        // 2. Tinh tong tien
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // 3. Áp dụng mã coupon
        double discountCoupon = cart.get().getCoupon() != null ? cart.get().getCoupon().getDiscountPrice() : 0;
        int loyaltyPoint = cart.get().getLoyaltyPoint();

        double finalPrice = totalPrice - discountCoupon - loyaltyPoint;

        // Lấy địa chỉ mặc định của user
        Address address = new Address();
        List<Address> addresses = addressRepository.findByUserId(user.getId());
        for (Address address1: addresses) {
            if (address1.isDefault()) {
                address = address1;
                break;
            }
        }

        // 4. Tạo đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setPurchaseDate(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        order.setTotalAmount(finalPrice);
        order.setCouponCode(cart.get().getCoupon() != null ? cart.get().getCoupon().getCode() : "");
        order.setWard(address.getWard());
        order.setWardCode(address.getWardCode());
        order.setDistrict(address.getDistrict());
        order.setDistrictCode(address.getDistrictCode());
        order.setProvince(address.getProvince());
        order.setProvinceCode(address.getProvinceCode());
        order.setAddressDetail(address.getAddressDetail());
        order.setStatus("Pending");

        orderRepository.save(order);

        // 5. Tạo OrderStatusHistory
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setStatus("Pending");
        orderStatusHistory.setOrder(order);
        orderStatusHistory.setUpdateAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        orderStatusHistoryRepository.save(orderStatusHistory);

        // 6. Tạo order item
        for (CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(item.getVariant());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
            orderItem.setImage(productImageResolver.getPrimaryImage(item.getVariant()));
            orderItemRepository.save(orderItem);
        }

        // 7. Cộng điểm loyalty mới
        int earnedPoints = (int) (totalPrice * 0.1);
        LoyaltyPoint loyaltyPoint1 = user.getLoyaltyPoint();
        if (loyaltyPoint1 == null) {
            loyaltyPoint1 = new LoyaltyPoint();
            loyaltyPoint1.setUser(user);
            loyaltyPoint1.setPoints(earnedPoints);
        }
        else {
            loyaltyPoint1.setPoints(loyaltyPoint1.getPoints() - loyaltyPoint + earnedPoints);
        }
        loyaltyPoint1.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        loyaltyPointRepository.save(loyaltyPoint1);

        // 8. Cập nhật lại quantity Coupon
        if (cart.get().getCoupon() != null) {
            String couponCode = cart.get().getCoupon().getCode();
            Optional<Coupon> coupon = couponRepository.findByCode(couponCode);
            if (coupon.isPresent()) {
                coupon.get().setQuantity(coupon.get().getQuantity() - 1);
                couponRepository.save(coupon.get());
            }
        }

        // 9. Cập nhật lại số lượng Inventory của productVariant
        //  - Lấy danh sách của các VariantId
        List<Integer> variantIds = cartItems.stream()
                .map(cartItem -> cartItem.getVariant().getId())
                .toList();

        List<Inventory> inventories = inventoryRepository.findByProductVariantIds(variantIds);

        for (CartItem item: cartItems) {
            int variantId = item.getVariant().getId();
            inventories.stream()
                    .filter(inv -> inv.getVariant().getId() == variantId)
                    .findFirst()
                    .ifPresent(inv -> inv.setQuantity(inv.getQuantity() - item.getQuantity()));
        }

        inventoryRepository.saveAll(inventories);

        // 10. Xóa giỏ hàng
        cartItemRepository.deleteAll(cartItems);
        cartRepository.delete(cart.get());


        // 11. Gửi email xác nhận đơn hàng
        emailService.sendOrderConfirmation(user.getEmail(), order, cartItems);

        return ResponseEntity.ok().body("Order placed successfully");

    }

    @Override
    public ResponseEntity<?> trackOrder(int orderId, String newStatus) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return ResponseEntity.badRequest().body("Order not found");
        }

        // Cập nhật trang thái
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrder(order.get());
        orderStatusHistory.setStatus(newStatus);
        orderStatusHistory.setUpdateAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        orderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory);

        order.get().setStatus(orderStatusHistory.getStatus());
        orderRepository.save(order.get());

        return ResponseEntity.ok().body("Order's status updated successfully");
    }

    @Override
    public ResponseEntity<?> getOrderStatus(int orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return ResponseEntity.badRequest().body("Order not found");
        }

        // Lấy list status theo orderId
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryRepository.findByOrderIdOrderByUpdateAtDesc(orderId);

        List<Map<String, Object>> response = orderStatusHistoryList.stream()
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("status", h.getStatus());
                    map.put("updateAt", h.getUpdateAt());
                    return map;
                })
                .toList();

        return ResponseEntity.ok().body(response);
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);


        return orders.map(
                order -> {
                    OrderResponse orderResponse = OrderResponse.builder()
                            .orderId(order.getId())
                            .fullName(order.getUser().getFullName())
                            .purchaseDate(order.getPurchaseDate())
                            .totalAmount(order.getTotalAmount())
                            .couponCode(order.getCouponCode() == null ? "" : order.getCouponCode())
                            .status(order.getStatus())
                            .build();

                    List<OrderItemResponse> orderItemResponses = order.getOrderItemList().stream()
                            .map(orderItem -> OrderItemResponse.builder()
                                    .orderItemId(orderItem.getId())
                                    .productVariantName(orderItem.getVariant().getName())
                                    .quantity(orderItem.getQuantity())
                                    .price(orderItem.getPrice())
                                    .image(orderItem.getImage())
                                    .build())
                            .toList();

                    orderResponse.setOrderItems(orderItemResponses);

                    return orderResponse;
                }
        );
    }

    @Override
    public Page<OrderResponse> getOrderTimeline(Pageable pageable, String filter, LocalDate starDate, LocalDate endDate) {
        LocalDateTime start, end;

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        switch (filter.toLowerCase()) {
            case "today":
                start = today.atStartOfDay();
                end = today.plusDays(1).atStartOfDay();
                break;
            case "yesterday":
                start = today.minusDays(1).atStartOfDay();
                end = today.atStartOfDay();
                break;
            case "this_week":
                start = today.with(DayOfWeek.MONDAY).atStartOfDay();
                end = today.plusDays(1).atStartOfDay();
                break;
            case "this_month":
                start = today.withDayOfMonth(1).atStartOfDay();
                end = today.plusDays(1).atStartOfDay();
                break;
            case "custom":
                start = starDate.atStartOfDay();
                end = endDate.plusDays(1).atStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Invalid filter: " + filter);
        }

        Page<Order> orders = orderRepository.findByPurchaseDateBetween(start, end, pageable);

        return orders.map(
                order -> {
                    OrderResponse orderResponse = OrderResponse.builder()
                            .orderId(order.getId())
                            .fullName(order.getUser().getFullName())
                            .purchaseDate(order.getPurchaseDate())
                            .totalAmount(order.getTotalAmount())
                            .couponCode(order.getCouponCode() == null ? "" : order.getCouponCode())
                            .status(order.getStatus())
                            .build();

                    List<OrderItemResponse> orderItemResponses = order.getOrderItemList().stream()
                            .map(orderItem -> OrderItemResponse.builder()
                                    .orderItemId(orderItem.getId())
                                    .productVariantName(orderItem.getVariant().getName())
                                    .quantity(orderItem.getQuantity())
                                    .price(orderItem.getPrice())
                                    .image(orderItem.getImage())
                                    .build())
                            .toList();

                    orderResponse.setOrderItems(orderItemResponses);

                    return orderResponse;
                }
        );
    }

    @Override
    public ResponseEntity<String> updateOrder(int orderId, OrderUpdate orderUpdate) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return ResponseEntity.badRequest().body("Order not found");
        }

        Optional<User> user = userRepository.findById(orderUpdate.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        order.get().setUser(user.get());
        order.get().setPurchaseDate(orderUpdate.getPurchaseDate());
        order.get().setTotalAmount(orderUpdate.getTotalAmount());
        order.get().setCouponCode(orderUpdate.getCouponCode());
        order.get().setStatus(orderUpdate.getStatus());

        // Update OrderItem
        List<OrderItem> updatedItem = new ArrayList<>();
        for (OrderItemUpdate orderItemUpdate: orderUpdate.getOrderItemUpdates()) {
            OrderItem item = null;
            if (String.valueOf(orderItemUpdate.getOrderItemId()) != null) {
                item = order.get().getOrderItemList().stream()
                        .filter(i -> i.getId() == orderItemUpdate.getOrderItemId())
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("OrderItem not found"));
            }

            Optional<ProductVariant> productVariant = productVariantRepository.findById(orderItemUpdate.getProductVariantId());
            item.setVariant(productVariant.get());
            item.setQuantity(orderItemUpdate.getQuantity());
            item.setPrice(orderItemUpdate.getPrice());
            updatedItem.add(item);
        }

        order.get().setOrderItemList(updatedItem);
        orderRepository.save(order.get());

        return ResponseEntity.ok().body("Order updated");
    }

    @Override
    public List<OrderResponse> getOrderHistory(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return null;
        }

        List<Order> orders = orderRepository.findByUserId(userId);

        // 👉 Sắp xếp đơn hàng theo ngày mua giảm dần (mới nhất lên đầu)
        orders.sort((o1, o2) -> o2.getPurchaseDate().compareTo(o1.getPurchaseDate()));

        return orders.stream()
                .map(order -> OrderResponse.builder()
                        .orderId(order.getId())
                        .fullName(user.get().getFullName())
                        .purchaseDate(order.getPurchaseDate())
                        .totalAmount(order.getTotalAmount())
                        .couponCode(order.getCouponCode() == null ? "" : order.getCouponCode())
                        .status(order.getStatus())
                        .address(AddressResponse.builder()
                                .isDefault(true)
                                .ward(order.getWard())
                                .wardCode(order.getWardCode())
                                .district(order.getDistrict())
                                .districtCode(order.getDistrictCode())
                                .province(order.getProvince())
                                .provinceCode(order.getProvinceCode())
                                .addressDetail(order.getAddressDetail())
                                .build())
                        .orderItems(order.getOrderItemList().stream()
                                .map(orderItem -> OrderItemResponse.builder()
                                        .orderItemId(orderItem.getId())
                                        .productVariantName(orderItem.getVariant().getName())
                                        .price(orderItem.getPrice())
                                        .quantity(orderItem.getQuantity())
                                        .image(orderItem.getImage())
                                        .build())
                                .toList())
                        .build())
                .toList();

    }

}
