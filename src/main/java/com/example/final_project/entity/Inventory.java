package com.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(name = "uk_inventory_variant", columnNames = "variant_id"))
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false, unique = true)
    private ProductVariant variant;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "reserved_quantity", nullable = false, columnDefinition = "integer default 0")
    @Builder.Default
    private int reservedQuantity = 0;

    @Column(name = "low_stock_threshold", nullable = false, columnDefinition = "integer default 0")
    @Builder.Default
    private int lowStockThreshold = 0;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    @Transient
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
}
