package com.example.final_project.service;

import com.example.final_project.dto.CategoryRequest;
import com.example.final_project.dto.CategoryResponse;
import com.example.final_project.dto.ProductResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    ResponseEntity<String> addCategory(CategoryRequest categoryRequest);

    CategoryResponse getCategoryById(int id);

    List<ProductResponse> getProductsByCategory(int id);

    ResponseEntity<String> deleteCategory(int categoryId);

    ResponseEntity<String> updateCategory(int categoryId, CategoryRequest categoryRequest);
}
