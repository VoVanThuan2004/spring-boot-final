package com.example.final_project.mapper;

import com.example.final_project.dto.response.CategoryResponse;
import com.example.final_project.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .isDeleted(category.isDeleted())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public List<CategoryResponse> mapToResponseList(List<Category> categories) {
        return categories.stream().map(this::mapToResponse).toList();
    }
}
