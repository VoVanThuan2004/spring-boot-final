package com.example.final_project.controller;

import com.example.final_project.dto.request.ProductFilterRequest;
import com.example.final_project.dto.request.ProductRequest;
import com.example.final_project.dto.response.ApiResponse;
import com.example.final_project.dto.response.ProductResponse;
import com.example.final_project.service.ProductService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(encoding = @Encoding(
                    name = "productRequest",
                    contentType = MediaType.APPLICATION_JSON_VALUE
            ))
    )
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @Valid @RequestPart("productRequest") ProductRequest productRequest,
            @RequestPart("file") MultipartFile file

    ) {
        ProductResponse productResponse = productService.addProduct(productRequest, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Created product successfully",
                productResponse
        ));
    }

    // Lấy list products
    @GetMapping("/api/v1/products/default")
    public List<ProductResponse> getAllProductsDefault() {
        return productService.getAllProductsDefault();
    }

    // Update products
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(encoding = @Encoding(
                    name = "productRequest",
                    contentType = MediaType.APPLICATION_JSON_VALUE
            ))
    )
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable int productId,
            @Valid @RequestPart("productRequest") ProductRequest productRequest,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        ProductResponse productResponse = productService.updateProduct(productId, productRequest, file);

        return ResponseEntity.ok(ApiResponse.success(
                "Product updated successfully",
                productResponse
        ));
    }

    // Xoá mềm product
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable int productId) {
        boolean deleted = productService.softDeleteProduct(productId);
        String message = deleted ? "Product deleted successfully" : "Product already archived";

        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @PatchMapping("/{productId}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> restoreProduct(@PathVariable int productId) {
        ProductResponse productResponse = productService.restoreProduct(productId);

        return ResponseEntity.ok(ApiResponse.success(
                "Product restored successfully",
                productResponse
        ));
    }

    // Sắp xếp theo tên - gía (tăng dần, giảm dần)
    @GetMapping("/api/v1/products")
    public Page<ProductResponse> getAllProducts(
            @RequestParam int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return productService.getAllProducts(page, size, sortBy, direction);
    }

    // Search & Filter theo price, category, brand,...
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getFilterProducts(
            @Valid @ModelAttribute @ParameterObject ProductFilterRequest request
    ) {
        Page<ProductResponse> productResponses = productService.getFilterProducts(request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Filter product successfully", productResponses));
    }

    // Get sản phẩm mới
    @GetMapping("/new")
    public List<ProductResponse> getNewProducts() {
        return productService.getNewProducts();
    }

    // Lây danh sách product bestseller
    @GetMapping("/best-seller")
    public List<ProductResponse> getAllProductsBestSeller() {
        return productService.getAllProductsBestSeller();
    }


}
