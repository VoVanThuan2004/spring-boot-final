package com.example.final_project.service;

import com.example.final_project.dto.InventoryRequest;
import com.example.final_project.dto.ProductResponse;
import com.example.final_project.dto.ProductVariantRequest;
import com.example.final_project.entity.ProductVariant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductVariantService {
    ResponseEntity<String> createProductVariant(ProductVariantRequest productVariantRequest, List<MultipartFile> files);

    ResponseEntity<String> updateQuantityProductVariant(InventoryRequest inventoryRequest);

    ResponseEntity<?> getProductVariants(int productId);

    ResponseEntity<String> deleteProductVariant(int productVariant_id);

    ResponseEntity<String> updateProductVariant(int productVariant_id, ProductVariantRequest productVariantRequest, List<MultipartFile> files);

    ResponseEntity<String> setDiscountProductVariant(int productVariantId, double discountPercent);

    List<ProductResponse> getListProductsDiscount();
}
