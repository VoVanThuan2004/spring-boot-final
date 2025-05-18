package com.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String variantName;
    private double costPrice;   // Giá gốc
    private double priceDiff;   // Gia chenh lech
    private String specs;       // Lưu thông số product (RAM, ROM,...)

    private LocalDateTime createdAt;

    private double discountPercent;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(mappedBy = "productVariant", cascade = CascadeType.ALL)
    private Inventory inventory;

    // Liên kết nhiều images
    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL)
    private List<Images> images;
}
