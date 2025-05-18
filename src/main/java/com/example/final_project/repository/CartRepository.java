package com.example.final_project.repository;

import com.example.final_project.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserId(int userId);
    Optional<Cart> findBySessionId(String sessionId);
}
