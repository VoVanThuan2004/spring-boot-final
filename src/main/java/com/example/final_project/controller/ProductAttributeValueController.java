package com.example.final_project.controller;

import com.example.final_project.dto.request.ProductAttributeValueRequest;
import com.example.final_project.dto.response.ApiResponse;
import com.example.final_project.dto.response.ProductAttributeValueImageResponse;
import com.example.final_project.dto.response.ProductAttributeValueResponse;
import com.example.final_project.service.ProductAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/{productId}/attributes/{attributeId}/values")
public class ProductAttributeValueController {

    private final ProductAttributeService attributeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAttributeValueResponse>>> getValues(
            @PathVariable int productId,
            @PathVariable int attributeId) {
        return ResponseEntity.ok(ApiResponse.success(attributeService.getValues(productId, attributeId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductAttributeValueResponse>> createValue(
            @PathVariable int productId,
            @PathVariable int attributeId,
            @Valid @RequestBody ProductAttributeValueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Attribute value created successfully",
                        attributeService.createValue(productId, attributeId, request)));
    }

    @PutMapping("/{valueId}")
    public ResponseEntity<ApiResponse<ProductAttributeValueResponse>> updateValue(
            @PathVariable int productId,
            @PathVariable int attributeId,
            @PathVariable int valueId,
            @Valid @RequestBody ProductAttributeValueRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Attribute value updated successfully",
                attributeService.updateValue(productId, attributeId, valueId, request)));
    }

    @DeleteMapping("/{valueId}")
    public ResponseEntity<ApiResponse<Void>> deleteValue(
            @PathVariable int productId,
            @PathVariable int attributeId,
            @PathVariable int valueId) {
        attributeService.deleteValue(productId, attributeId, valueId);
        return ResponseEntity.ok(ApiResponse.success("Attribute value deleted successfully", null));
    }

    @PostMapping(value = "/{valueId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<ProductAttributeValueImageResponse>>> addImages(
            @PathVariable int productId,
            @PathVariable int attributeId,
            @PathVariable int valueId,
            @RequestPart("files") List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Images uploaded successfully",
                        attributeService.addImages(productId, attributeId, valueId, files)));
    }

    @PatchMapping("/{valueId}/images/{imageId}/primary")
    public ResponseEntity<ApiResponse<ProductAttributeValueImageResponse>> setPrimaryImage(
            @PathVariable int productId,
            @PathVariable int attributeId,
            @PathVariable int valueId,
            @PathVariable int imageId) {
        return ResponseEntity.ok(ApiResponse.success("Primary image updated successfully",
                attributeService.setPrimaryImage(productId, attributeId, valueId, imageId)));
    }

    @DeleteMapping("/{valueId}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable int productId,
            @PathVariable int attributeId,
            @PathVariable int valueId,
            @PathVariable int imageId) {
        attributeService.deleteImage(productId, attributeId, valueId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
    }
}
