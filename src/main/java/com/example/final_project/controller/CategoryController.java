package com.example.final_project.controller;

import com.example.final_project.dto.CategoryRequest;
import com.example.final_project.dto.CategoryResponse;
import com.example.final_project.dto.ProductResponse;
import com.example.final_project.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5000")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/api/v1/category")
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/api/v1/category")
    public ResponseEntity<String> addCategory(@RequestBody CategoryRequest categoryRequest) {
        return categoryService.addCategory(categoryRequest);
    }

    @DeleteMapping("/api/v1/category/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable int categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @PutMapping("/api/v1/category/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable int categoryId, @RequestBody CategoryRequest categoryRequest) {
        return categoryService.updateCategory(categoryId, categoryRequest);
    }

    @GetMapping("/api/v1/category/{id}")
    public CategoryResponse getCategoryById(@PathVariable int id) {
        return categoryService.getCategoryById(id);
    }

    // Lấy danh sách sản phẩm thuộc category
    @GetMapping("/api/v1/category/products/{id}")
    public List<ProductResponse> getProductsByCategory(@PathVariable int id) {
        return categoryService.getProductsByCategory(id);
    }
}
