package com.example.final_project.service;

import com.example.final_project.dto.request.CategoryRequest;
import com.example.final_project.dto.response.CategoryResponse;
import com.example.final_project.dto.response.ProductResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse addCategory(CategoryRequest categoryRequest);

    CategoryResponse getCategoryById(int id);

    List<ProductResponse> getProductsByCategory(int id);

    void deleteCategory(int categoryId);

    CategoryResponse restoreCategory(int categoryId);

    CategoryResponse updateCategory(int categoryId, CategoryRequest categoryRequest);
}
