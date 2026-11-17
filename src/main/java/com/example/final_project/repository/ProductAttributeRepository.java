package com.example.final_project.repository;

import com.example.final_project.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Integer> {

    List<ProductAttribute> findByProductIdOrderBySortOrderAsc(int productId);

    Optional<ProductAttribute> findByIdAndProductId(int id, int productId);

    Optional<ProductAttribute> findByProductIdAndIsVisualTrue(int productId);
}
