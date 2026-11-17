package com.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "product_attribute",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_product_attribute_name",
                columnNames = {"product_id", "name"}))
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "is_visual", nullable = false)
    @Builder.Default
    private boolean isVisual = false;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductAttributeValue> values;
}
