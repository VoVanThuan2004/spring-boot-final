package com.example.final_project.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VariantAttributeValueId implements Serializable {

    private int variantId;

    private int attributeValueId;
}
