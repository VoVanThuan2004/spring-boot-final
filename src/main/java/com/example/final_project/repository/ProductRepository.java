package com.example.final_project.repository;

import com.example.final_project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
//    @Query("SELECT p.category.name, SUM(oi.quantity) FROM Product p " +
//            "JOIN p.productVariantList v JOIN v.orderItems oi GROUP BY p.category.name")
//    List<Object[]> getProductCategoryStats();

    @Query("SELECT p FROM Product p WHERE p.discountPercent != 1")
    List<Product> findByDiscountPercent();

    @Query("SELECT p FROM Product p WHERE p.createdAt BETWEEN :startOfMonth AND :endOfMonth")
    List<Product> findProductsCreatedThisMonth(
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth
    );
}
