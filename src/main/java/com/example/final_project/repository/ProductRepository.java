package com.example.final_project.repository;

import com.example.final_project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v " +
            "WHERE v.compareAtPrice IS NOT NULL AND v.compareAtPrice > v.price")
    List<Product> findByDiscountPercent();

    @Query("SELECT p FROM Product p WHERE p.createdAt BETWEEN :startOfMonth AND :endOfMonth")
    List<Product> findProductsCreatedThisMonth(
            @Param("startOfMonth") Instant startOfMonth,
            @Param("endOfMonth") Instant endOfMonth
    );
}
