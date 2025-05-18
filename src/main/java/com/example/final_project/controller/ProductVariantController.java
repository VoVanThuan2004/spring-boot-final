package com.example.final_project.controller;

import com.example.final_project.dto.InventoryRequest;
import com.example.final_project.dto.ProductResponse;
import com.example.final_project.dto.ProductVariantRequest;
import com.example.final_project.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5000")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping("/api/v1/productVariants")
    public ResponseEntity<String> createProductVariant(
            @RequestPart("productVariant") ProductVariantRequest productVariantRequest,
            @RequestPart("file") List<MultipartFile> files
            ) {
        return productVariantService.createProductVariant(productVariantRequest, files);
    }

    // Update productVariant
    @PutMapping("/api/v1/productVariants/{productVariant_id}")
    public ResponseEntity<String> updateProductVariant(
            @PathVariable int productVariant_id,
            @RequestPart("productVariant") ProductVariantRequest productVariantRequest,
            @RequestPart(value = "file", required = false) List<MultipartFile> files
    ) {
        return productVariantService.updateProductVariant(productVariant_id, productVariantRequest, files);
    }

    // Xoá productVariant
    @DeleteMapping("/api/v1/productVariants/{productVariant_id}")
    public ResponseEntity<String> deleteProductVariant(@PathVariable int productVariant_id) {
        return productVariantService.deleteProductVariant(productVariant_id);
    }

    // Trả về danh sách productVariants (các biến thể) theo product_id
    @GetMapping("/api/v1/productVariants/{product_id}")
    public ResponseEntity<?> getProductVariants(@PathVariable int product_id) {
        return productVariantService.getProductVariants(product_id);
    }

    // Update quantity in inventory
    @PutMapping("/api/v1/update/quantity")
    public ResponseEntity<String> updateQuantityProductVariant(@RequestBody InventoryRequest inventoryRequest) {
        return productVariantService.updateQuantityProductVariant(inventoryRequest);
    }

    // Update giảm giá cho sản phẩm
    @PutMapping("/api/v1/productVariants/update-discount/{productVariant_id}/{discountPercent}")
    public ResponseEntity<String> setDiscountProductVariant(
            @PathVariable int productVariant_id,
            @PathVariable double discountPercent
    ) {
        return productVariantService.setDiscountProductVariant(productVariant_id, discountPercent);
    }


    // Lấy list product giảm giá
    @GetMapping("/api/v1/products/discount")
    public List<ProductResponse> getListProductsDiscount() {
        return productVariantService.getListProductsDiscount();
    }

}
