package com.example.final_project.repository;

import com.example.final_project.entity.OrderItem;
import com.example.final_project.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("SELECT oi.variant.product.name, SUM(oi.quantity), SUM(oi.price * oi.quantity) FROM OrderItem oi GROUP BY oi.variant.product.name ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getBestSellingProducts(int limit);

    @Query("SELECT p.category.name, SUM(oi.quantity) " +
            "FROM OrderItem oi " +
            "JOIN oi.variant v " +
            "JOIN v.product p " +
            "GROUP BY p.category.name")
    List<Object[]> fetchProductCategoryStats();

    @Query("SELECT c.name, SUM(oi.quantity) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.variant v " +
            "JOIN v.product p " +
            "JOIN p.category c " +
            "WHERE o.purchaseDate BETWEEN :from AND :to " +
            "GROUP BY c.name")
    List<Object[]> fetchProductCategoryStatsBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.purchaseDate BETWEEN :from AND :to")
    List<OrderItem> findByOrderPurchaseDateBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT oi.variant.product FROM OrderItem oi GROUP BY oi.variant.product ORDER BY SUM(oi.quantity) DESC")
    List<Product> getAllProductsBestSeller(Pageable pageable);

}
