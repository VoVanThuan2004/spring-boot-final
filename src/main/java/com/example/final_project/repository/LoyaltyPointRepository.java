package com.example.final_project.repository;

import com.example.final_project.entity.LoyaltyPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Integer> {
    Optional<LoyaltyPoint> findByUserId(int userId);
}
