package com.example.final_project.mapper;

import com.example.final_project.dto.response.ProductResponse;
import com.example.final_project.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .image(product.getImage())
                .description(product.getDescription())
                .specs(product.getSpecs())
                .createdAt(product.getCreatedAt())
                .discountPercent(product.getDiscountPercent())
                .categoryName(product.getCategory() == null ? null : product.getCategory().getName())
                .brandName(product.getBrand() == null ? null : product.getBrand().getName())
                .build();
    }

    public List<ProductResponse> mapToResponseList(List<Product> products) {
        return products.stream().map(this::mapToResponse).toList();
    }
}
