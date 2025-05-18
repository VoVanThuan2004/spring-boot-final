package com.example.final_project.repository;

import com.example.final_project.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    Optional<Rating> findByUserIdAndProductId(int userId, int productId);
    List<Rating> findByProductId(int productId);
}
