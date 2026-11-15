package com.example.final_project.service;

import com.example.final_project.dto.request.BrandRequest;
import com.example.final_project.dto.response.BrandResponse;
import com.example.final_project.dto.response.ProductResponse;
import com.example.final_project.entity.Brand;
import com.example.final_project.exception.BadRequestException;
import com.example.final_project.exception.ResourceNotFoundException;
import com.example.final_project.mapper.BrandMapper;
import com.example.final_project.mapper.ProductMapper;
import com.example.final_project.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ProductMapper productMapper;

    @Override
    public BrandResponse addBrand(BrandRequest request) {
        String name = request.getName().trim();
        if (brandRepository.existsByNameIgnoreCaseAndIsDeletedFalse(name)) {
            throw new BadRequestException("Brand name already exists");
        }
        return brandMapper.mapToResponse(brandRepository.save(Brand.builder().name(name).build()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return brandMapper.mapToResponseList(brandRepository.findAllByIsDeletedFalse());
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(int brandId) {
        return brandMapper.mapToResponse(findBrand(brandId));
    }

    @Override
    public void deleteBrand(int brandId) {
        Brand brand = findBrand(brandId);
        brand.setDeleted(true);
        brandRepository.save(brand);
    }

    @Override
    public BrandResponse restoreBrand(int id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        if (!brand.isDeleted()) {
            throw new BadRequestException("Brand is not deleted");
        }
        if (brandRepository.existsByNameIgnoreCaseAndIsDeletedFalse(brand.getName())) {
            throw new BadRequestException("An active brand with the same name already exists");
        }
        brand.setDeleted(false);
        return brandMapper.mapToResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByBrandId(int id) {
        return productMapper.mapToResponseList(findBrand(id).getProducts());
    }

    @Override
    public BrandResponse updateBrand(int id, BrandRequest request) {
        Brand brand = findBrand(id);
        String name = request.getName().trim();
        if (!brand.getName().equalsIgnoreCase(name)
                && brandRepository.existsByNameIgnoreCaseAndIsDeletedFalse(name)) {
            throw new BadRequestException("Brand name already exists");
        }
        brand.setName(name);
        return brandMapper.mapToResponse(brandRepository.save(brand));
    }

    private Brand findBrand(int id) {
        return brandRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
    }
}
