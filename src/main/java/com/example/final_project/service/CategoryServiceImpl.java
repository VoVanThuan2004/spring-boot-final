package com.example.final_project.service;

import com.example.final_project.dto.request.CategoryRequest;
import com.example.final_project.dto.response.CategoryResponse;
import com.example.final_project.dto.response.ProductResponse;
import com.example.final_project.entity.Category;
import com.example.final_project.exception.BadRequestException;
import com.example.final_project.exception.ResourceNotFoundException;
import com.example.final_project.mapper.CategoryMapper;
import com.example.final_project.mapper.ProductMapper;
import com.example.final_project.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.mapToResponseList(categoryRepository.findAllByIsDeletedFalse());
    }

    @Override
    public CategoryResponse addCategory(CategoryRequest request) {
        String name = request.getName().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndIsDeletedFalse(name)) {
            throw new BadRequestException("Category name already exists");
        }
        return categoryMapper.mapToResponse(categoryRepository.save(Category.builder().name(name).build()));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(int id) {
        return categoryMapper.mapToResponse(findCategory(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(int id) {
        return productMapper.mapToResponseList(findCategory(id).getProducts());
    }

    @Override
    public void deleteCategory(int id) {
        Category category = findCategory(id);
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse restoreCategory(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        if (!category.isDeleted()) {
            throw new BadRequestException("Category is not deleted");
        }
        if (categoryRepository.existsByNameIgnoreCaseAndIsDeletedFalse(category.getName())) {
            throw new BadRequestException("An active category with the same name already exists");
        }
        category.setDeleted(false);
        return categoryMapper.mapToResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse updateCategory(int id, CategoryRequest request) {
        Category category = findCategory(id);
        String name = request.getName().trim();
        if (!category.getName().equalsIgnoreCase(name)
                && categoryRepository.existsByNameIgnoreCaseAndIsDeletedFalse(name)) {
            throw new BadRequestException("Category name already exists");
        }
        category.setName(name);
        return categoryMapper.mapToResponse(categoryRepository.save(category));
    }

    private Category findCategory(int id) {
        return categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
}
