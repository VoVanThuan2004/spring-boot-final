package com.example.final_project.controller;

import com.example.final_project.dto.BrandRequest;
import com.example.final_project.dto.BrandResponse;
import com.example.final_project.dto.ProductResponse;
import com.example.final_project.entity.Brand;
import com.example.final_project.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5000")
public class BrandController {
    private final BrandService brandService;

    @PostMapping("/api/v1/brand")
    public ResponseEntity<String> addBrand(@RequestBody BrandRequest brandRequest) {
        return brandService.addBrand(brandRequest);
    }

    @GetMapping("/api/v1/brand")
    public List<BrandResponse> getAllBrands() {
        return brandService.getAllBrands();
    }

    // Get list products theo brand
    @GetMapping("/api/v1/brand/products/{id}")
    public List<ProductResponse> getProductsByBrandId(@PathVariable int id) {
        return brandService.getProductsByBrandId(id);
    }

    @PutMapping("/api/v1/brand/{id}")
    public ResponseEntity<String> updateBrand(@PathVariable int id, @RequestBody BrandRequest brandRequest) {
        return brandService.updateBrand(id, brandRequest);
    }

    @DeleteMapping("/api/v1/brand/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable int id) {
        return brandService.deleteBrand(id);
    }
}
