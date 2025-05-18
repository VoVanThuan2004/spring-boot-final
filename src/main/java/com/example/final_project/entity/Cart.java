package com.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String sessionId; //không đăng nhập

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;      // nullable nếu là guest

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItemList;

//    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private int loyaltyPoint;
}
