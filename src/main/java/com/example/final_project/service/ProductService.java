package com.example.final_project.service;

import com.example.final_project.dto.ProductRequest;
import com.example.final_project.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ResponseEntity<String> addProduct(ProductRequest productRequest, MultipartFile file);

    Page<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction);

    Page<ProductResponse> getFilterProducts(String keyword, List<Integer> categoryIds, List<Integer> brandIds, double minPrice, double maxPrice, int page, int size, String sortBy, String direction);

    List<ProductResponse> getAllProductsDefault();

    ResponseEntity<String> updateProduct(int productId, ProductRequest productRequest, MultipartFile file);

    ResponseEntity<String> deleteProduct(int productId);

    List<ProductResponse> getNewProducts();

    List<ProductResponse> getAllProductsBestSeller();
}
