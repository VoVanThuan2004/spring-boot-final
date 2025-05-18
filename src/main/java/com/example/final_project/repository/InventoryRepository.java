package com.example.final_project.repository;

import com.example.final_project.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByProductVariantId(int productVariantId);

    @Query("SELECT i FROM Inventory i WHERE i.productVariant.id IN :variantIds")
    List<Inventory> findByProductVariantIds(@Param("variantIds") List<Integer> variantIds);}
