package com.example.final_project.mapper;

import com.example.final_project.dto.response.ProductResponse;
import com.example.final_project.entity.Product;
import com.example.final_project.entity.ProductVariant;
import com.example.final_project.service.ProductImageResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ProductImageResolver productImageResolver;

    public ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .specs(product.getSpecifications())
                .slug(product.getSlug())
                .createdAt(product.getCreatedAt())
                .imageUrl(product.getImageUrl())
                .build();
    }

    public List<ProductResponse> mapToResponseList(List<Product> products) {
        return products.stream().map(this::mapToResponse).toList();
    }

    private boolean hasDiscount(ProductVariant variant) {
        return variant.getCompareAtPrice() != null
                && variant.getPrice() != null
                && variant.getCompareAtPrice().compareTo(variant.getPrice()) > 0;
    }

    private double discountPercent(ProductVariant variant) {
        return variant.getCompareAtPrice()
                .subtract(variant.getPrice())
                .divide(variant.getCompareAtPrice(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private LocalDateTime toLocalDateTime(Product product) {
        return product.getCreatedAt() == null
                ? null
                : LocalDateTime.ofInstant(product.getCreatedAt(), ZoneId.systemDefault());
    }
}
