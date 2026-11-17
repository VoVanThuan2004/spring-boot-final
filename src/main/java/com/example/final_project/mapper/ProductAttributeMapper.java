package com.example.final_project.mapper;

import com.example.final_project.dto.response.ProductAttributeResponse;
import com.example.final_project.dto.response.ProductAttributeValueImageResponse;
import com.example.final_project.dto.response.ProductAttributeValueResponse;
import com.example.final_project.entity.ProductAttribute;
import com.example.final_project.entity.ProductAttributeValue;
import com.example.final_project.entity.ProductAttributeValueImage;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProductAttributeMapper {

    public ProductAttributeResponse mapToResponse(ProductAttribute attribute) {
        List<ProductAttributeValueResponse> values = attribute.getValues() == null
                ? List.of()
                : attribute.getValues().stream()
                .sorted(Comparator.comparingInt(ProductAttributeValue::getSortOrder)
                        .thenComparingInt(ProductAttributeValue::getId))
                .map(this::mapToValueResponse)
                .toList();

        return ProductAttributeResponse.builder()
                .id(attribute.getId())
                .productId(attribute.getProduct().getId())
                .name(attribute.getName())
                .isVisual(attribute.isVisual())
                .sortOrder(attribute.getSortOrder())
                .values(values)
                .build();
    }

    public ProductAttributeValueResponse mapToValueResponse(ProductAttributeValue value) {
        List<ProductAttributeValueImageResponse> images = value.getImages() == null
                ? List.of()
                : value.getImages().stream()
                .sorted(Comparator.comparing(ProductAttributeValueImage::isPrimary).reversed()
                        .thenComparingInt(ProductAttributeValueImage::getSortOrder)
                        .thenComparingInt(ProductAttributeValueImage::getId))
                .map(this::mapToImageResponse)
                .toList();

        return ProductAttributeValueResponse.builder()
                .id(value.getId())
                .attributeId(value.getAttribute().getId())
                .value(value.getValue())
                .sortOrder(value.getSortOrder())
                .images(images)
                .build();
    }

    public ProductAttributeValueImageResponse mapToImageResponse(ProductAttributeValueImage image) {
        return ProductAttributeValueImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .sortOrder(image.getSortOrder())
                .isPrimary(image.isPrimary())
                .build();
    }
}
