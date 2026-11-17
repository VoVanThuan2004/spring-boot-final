package com.example.final_project.service;

import com.example.final_project.entity.ProductAttributeValueImage;
import com.example.final_project.entity.ProductVariant;
import com.example.final_project.repository.ProductAttributeValueImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductImageResolver {

    private final ProductAttributeValueImageRepository imageRepository;

    public List<String> getImages(ProductVariant variant) {
        if (variant == null) {
            return List.of();
        }
        return imageRepository.findVisualImagesByVariantId(variant.getId())
                .stream()
                .map(ProductAttributeValueImage::getImageUrl)
                .toList();
    }

    public String getPrimaryImage(ProductVariant variant) {
        return getImages(variant).stream().findFirst().orElse(null);
    }
}
