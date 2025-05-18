package com.example.final_project.service;

import com.example.final_project.dto.BrandRequest;
import com.example.final_project.dto.BrandResponse;
import com.example.final_project.dto.ProductResponse;
import com.example.final_project.entity.Brand;
import com.example.final_project.entity.Product;
import com.example.final_project.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;


    @Override
    public ResponseEntity<String> addBrand(BrandRequest brandRequest) {
        if (brandRequest == null) {
            return ResponseEntity.badRequest().body("Please provide a valid brand");
        }
        Brand brand = Brand.builder().name(brandRequest.getName()).build();
        brandRepository.save(brand);
        return ResponseEntity.ok().body("Successfully added brand");
    }

    @Override
    public List<BrandResponse> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();

        List<BrandResponse> brandResponses = new ArrayList<>();
        for (Brand brand : brands) {
            BrandResponse brandResponse = BrandResponse.builder()
                    .id(brand.getId())
                    .name(brand.getName())
                    .build();

            brandResponses.add(brandResponse);
        }
        return brandResponses;
    }

    @Override
    public ResponseEntity<String> deleteBrand(int id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            brandRepository.delete(brand.get());
            return ResponseEntity.ok().body("Successfully deleted brand");
        }
        return ResponseEntity.badRequest().body("Brand not found");
    }

    @Override
    public List<ProductResponse> getProductsByBrandId(int id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            List<Product> products = brand.get().getProducts();

            return products.stream()
                    .map(product -> ProductResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .price(product.getPrice())
                            .image(product.getImage())
                            .description(product.getDescription())
                            .specs(product.getSpecs())
                            .build())
                    .toList();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> updateBrand(int id, BrandRequest brandRequest) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isEmpty()) {
            return ResponseEntity.badRequest().body("Brand not found");
        }

        brand.get().setName(brandRequest.getName());
        brandRepository.save(brand.get());
        return ResponseEntity.ok().body("Successfully updated brand");
    }
}
