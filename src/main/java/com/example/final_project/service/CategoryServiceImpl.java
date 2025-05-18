package com.example.final_project.service;

import com.example.final_project.dto.CategoryRequest;
import com.example.final_project.dto.CategoryResponse;
import com.example.final_project.dto.ProductResponse;
import com.example.final_project.entity.Category;
import com.example.final_project.entity.Product;
import com.example.final_project.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoriesResponse = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponse categoryResponse = CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();

            categoriesResponse.add(categoryResponse);
        }

        return categoriesResponse;
    }

    @Override
    public ResponseEntity<String> addCategory(CategoryRequest categoryRequest) {
        if (categoryRequest == null) {
            return ResponseEntity.badRequest().body("Please provide a valid category");
        }

        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();
        categoryRepository.save(category);

        return ResponseEntity.ok().body("Category added successfully");
    }

    @Override
    public CategoryResponse getCategoryById(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(value -> CategoryResponse.builder()
                .id(value.getId())
                .name(value.getName())
                .build()).orElse(null);
    }

    @Override
    public List<ProductResponse> getProductsByCategory(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        List<Product> products = category.get().getProducts();

        return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .image(product.getImage())
                        .price(product.getPrice())
                        .specs(product.getSpecs())
                        .description(product.getDescription())
                        .build())
                .toList();
    }

    @Override
    public ResponseEntity<String> deleteCategory(int categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body("Category not found");
        }

        categoryRepository.delete(category.get());
        return ResponseEntity.ok().body("Category deleted successfully");
    }

    @Override
    public ResponseEntity<String> updateCategory(int categoryId, CategoryRequest categoryRequest) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body("Category not found");
        }

        category.get().setName(categoryRequest.getName());
        categoryRepository.save(category.get());
        return ResponseEntity.ok().body("Category updated successfully");
    }
}
