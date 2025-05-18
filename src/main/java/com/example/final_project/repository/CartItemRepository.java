package com.example.final_project.repository;

import com.example.final_project.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndVariantId(Integer cartId, Integer productId);
    Optional<CartItem> findByCartIdAndId(Integer cartId, Integer id);

    List<CartItem> findByCartId(int id);
}
