package com.example.final_project.service;

import com.example.final_project.dto.request.BrandRequest;
import com.example.final_project.dto.response.BrandResponse;
import com.example.final_project.dto.response.ProductResponse;
import java.util.List;

public interface BrandService {
    BrandResponse addBrand(BrandRequest brandRequest);

    List<BrandResponse> getAllBrands();

    BrandResponse getBrandById(int id);

    void deleteBrand(int id);

    BrandResponse restoreBrand(int id);

    List<ProductResponse> getProductsByBrandId(int id);

    BrandResponse updateBrand(int id, BrandRequest brandRequest);
}
