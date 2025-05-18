package com.example.final_project.repository;

import com.example.final_project.entity.Order;
import com.example.final_project.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    @Query("SELECT o FROM Order o WHERE o.purchaseDate >= :start AND o.purchaseDate < :end")
    Page<Order> findByPurchaseDateBetween(@Param("start") LocalDateTime startDate, @Param("end") LocalDateTime endDate, Pageable pageable);

    List<Order> findByStatus(String status);

    // Tính tổng doanh thu
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status= :status")
    double sumTotalAmountByStatus(String status);

    // Tính doanh thu theo tháng trong năm
    @Query("SELECT MONTH(o.purchaseDate) as month, SUM(o.totalAmount) as revenue " +
            "FROM Order o WHERE YEAR(o.purchaseDate) = :year GROUP BY MONTH(o.purchaseDate)")
    List<Map<String, Object>> getRevenueTrendByYear(int year);

    // Xem lich su don hang theo user
    List<Order> findByUserId(int userId);


    /*
        Advanced Dashboard
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.purchaseDate BETWEEN :from AND :to")
    int countOrdersBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Get danh sach order theo thoi gian
    @Query("SELECT o FROM Order o WHERE o.purchaseDate BETWEEN :from AND :to")
    List<Order> findOrdersBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
