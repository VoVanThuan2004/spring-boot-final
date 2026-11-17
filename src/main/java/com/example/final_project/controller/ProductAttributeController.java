package com.example.final_project.controller;

import com.example.final_project.dto.request.ProductAttributeRequest;
import com.example.final_project.dto.response.ApiResponse;
import com.example.final_project.dto.response.ProductAttributeResponse;
import com.example.final_project.service.ProductAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/{productId}/attributes")
public class ProductAttributeController {

    private final ProductAttributeService attributeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAttributeResponse>>> getAttributes(
            @PathVariable int productId) {
        return ResponseEntity.ok(ApiResponse.success(attributeService.getAttributes(productId)));
    }

    @GetMapping("/{attributeId}")
    public ResponseEntity<ApiResponse<ProductAttributeResponse>> getAttribute(
            @PathVariable int productId,
            @PathVariable int attributeId) {
        return ResponseEntity.ok(ApiResponse.success(attributeService.getAttribute(productId, attributeId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductAttributeResponse>> createAttribute(
            @PathVariable int productId,
            @Valid @RequestBody ProductAttributeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Attribute created successfully",
                        attributeService.createAttribute(productId, request)));
    }

    @PutMapping("/{attributeId}")
    public ResponseEntity<ApiResponse<ProductAttributeResponse>> updateAttribute(
            @PathVariable int productId,
            @PathVariable int attributeId,
            @Valid @RequestBody ProductAttributeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Attribute updated successfully",
                attributeService.updateAttribute(productId, attributeId, request)));
    }

    @DeleteMapping("/{attributeId}")
    public ResponseEntity<ApiResponse<Void>> deleteAttribute(
            @PathVariable int productId,
            @PathVariable int attributeId) {
        attributeService.deleteAttribute(productId, attributeId);
        return ResponseEntity.ok(ApiResponse.success("Attribute deleted successfully", null));
    }
}
