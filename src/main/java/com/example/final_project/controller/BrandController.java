package com.example.final_project.controller;

import com.example.final_project.dto.request.BrandRequest;
import com.example.final_project.dto.response.BrandResponse;
import com.example.final_project.dto.response.ApiResponse;
import com.example.final_project.dto.response.ProductResponse;
import com.example.final_project.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandController {
    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> addBrand(@Valid @RequestBody BrandRequest brandRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Brand created successfully", brandService.addBrand(brandRequest)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        return ResponseEntity.ok(ApiResponse.success(brandService.getAllBrands()));
    }

    // Get list products theo brand
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success(brandService.getBrandById(id)));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByBrandId(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success(brandService.getProductsByBrandId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(@PathVariable int id,
                                                                  @Valid @RequestBody BrandRequest brandRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                "Brand updated successfully", brandService.updateBrand(id, brandRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable int id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully", null));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<BrandResponse>> restoreBrand(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Brand restored successfully", brandService.restoreBrand(id)));
    }
}
