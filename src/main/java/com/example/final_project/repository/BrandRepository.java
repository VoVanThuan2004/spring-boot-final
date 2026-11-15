package com.example.final_project.repository;

import com.example.final_project.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    List<Brand> findAllByIsDeletedFalse();

    Optional<Brand> findByIdAndIsDeletedFalse(Integer id);

    boolean existsByNameIgnoreCaseAndIsDeletedFalse(String name);
}
