package com.example.final_project.repository;

import com.example.final_project.entity.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Integer> {

    List<ProductAttributeValue> findByAttributeIdOrderBySortOrderAsc(int attributeId);

    Optional<ProductAttributeValue> findByIdAndAttributeId(int id, int attributeId);

    Optional<ProductAttributeValue> findByAttributeIdAndValueIgnoreCase(int attributeId, String value);
}
