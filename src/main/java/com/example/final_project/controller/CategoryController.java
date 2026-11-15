package com.example.final_project.controller;

import com.example.final_project.dto.request.CategoryRequest;
import com.example.final_project.dto.response.CategoryResponse;
import com.example.final_project.dto.response.ApiResponse;
import com.example.final_project.dto.response.ProductResponse;
import com.example.final_project.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(
            @Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Category created successfully",
                        categoryService.addCategory(categoryRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable int id,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                "Category updated successfully", categoryService.updateCategory(id, categoryRequest)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryById(id)));
    }

    // Lấy danh sách sản phẩm thuộc category
    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getProductsByCategory(id)));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<CategoryResponse>> restoreCategory(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Category restored successfully", categoryService.restoreCategory(id)));
    }
}
