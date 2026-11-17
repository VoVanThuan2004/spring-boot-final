package com.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variant_attribute_value",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vav_variant_value",
                        columnNames = {"variant_id", "attribute_value_id"}),
                @UniqueConstraint(
                        name = "uk_vav_variant_attribute",
                        columnNames = {"variant_id", "attribute_id"})
        })
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VariantAttributeValue {

    @EmbeddedId
    private VariantAttributeValueId id;

    @MapsId("variantId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @MapsId("attributeValueId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_value_id", nullable = false)
    private ProductAttributeValue attributeValue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_id", nullable = false)
    private ProductAttribute attribute;
}
