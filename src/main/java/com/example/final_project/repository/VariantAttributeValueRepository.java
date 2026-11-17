package com.example.final_project.repository;

import com.example.final_project.entity.VariantAttributeValue;
import com.example.final_project.entity.VariantAttributeValueId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantAttributeValueRepository extends JpaRepository<VariantAttributeValue, VariantAttributeValueId> {

    List<VariantAttributeValue> findByVariantId(int variantId);

    boolean existsByAttributeId(int attributeId);

    boolean existsByAttributeValueId(int attributeValueId);
}
