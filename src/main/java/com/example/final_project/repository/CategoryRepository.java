package com.example.final_project.repository;

import com.example.final_project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByIsDeletedFalse();

    Optional<Category> findByIdAndIsDeletedFalse(Integer id);

    boolean existsByNameIgnoreCaseAndIsDeletedFalse(String name);
}
