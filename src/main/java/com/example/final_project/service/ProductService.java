package com.example.final_project.service;

import com.example.final_project.dto.request.ProductFilterRequest;
import com.example.final_project.dto.request.ProductRequest;
import com.example.final_project.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse addProduct(ProductRequest productRequest, MultipartFile file);

    Page<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction);

    Page<ProductResponse> getFilterProducts(ProductFilterRequest request);

    List<ProductResponse> getAllProductsDefault();

    ProductResponse updateProduct(int productId, ProductRequest productRequest, MultipartFile file);

    boolean softDeleteProduct(int productId);

    ProductResponse restoreProduct(int productId);

    List<ProductResponse> getNewProducts();

    List<ProductResponse> getAllProductsBestSeller();
}
