package com.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")   // nullable nếu là khách chưa đăng nhập
    private User user;

    private LocalDateTime purchaseDate;

    private double totalAmount;

    private String couponCode;      // Lưu mã giảm gia

    private String status;

    // Địa chỉ giao hàng
    private String wardCode;
    private String ward;
    private String districtCode;
    private String district;
    private String provinceCode;
    private String province;
    private String addressDetail;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItemList;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderStatusHistory> orderStatusHistoryList;
}
