package com.example.final_project.service;

import com.example.final_project.dto.BrandRequest;
import com.example.final_project.dto.BrandResponse;
import com.example.final_project.dto.ProductResponse;
import com.example.final_project.entity.Brand;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BrandService {
    ResponseEntity<String> addBrand(BrandRequest brandRequest);

    List<BrandResponse> getAllBrands();

    ResponseEntity<String> deleteBrand(int id);

    List<ProductResponse> getProductsByBrandId(int id);

    ResponseEntity<String> updateBrand(int id, BrandRequest brandRequest);
}
