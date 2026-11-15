package com.example.final_project.mapper;

import com.example.final_project.dto.response.BrandResponse;
import com.example.final_project.entity.Brand;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BrandMapper {

    public BrandResponse mapToResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .isDeleted(brand.isDeleted())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }

    public List<BrandResponse> mapToResponseList(List<Brand> brands) {
        return brands.stream().map(this::mapToResponse).toList();
    }
}
