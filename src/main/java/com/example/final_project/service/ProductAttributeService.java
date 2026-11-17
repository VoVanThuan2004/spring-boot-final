package com.example.final_project.service;

import com.example.final_project.dto.request.ProductAttributeRequest;
import com.example.final_project.dto.request.ProductAttributeValueRequest;
import com.example.final_project.dto.response.ProductAttributeResponse;
import com.example.final_project.dto.response.ProductAttributeValueImageResponse;
import com.example.final_project.dto.response.ProductAttributeValueResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductAttributeService {

    List<ProductAttributeResponse> getAttributes(int productId);

    ProductAttributeResponse getAttribute(int productId, int attributeId);

    ProductAttributeResponse createAttribute(int productId, ProductAttributeRequest request);

    ProductAttributeResponse updateAttribute(int productId, int attributeId, ProductAttributeRequest request);

    void deleteAttribute(int productId, int attributeId);

    List<ProductAttributeValueResponse> getValues(int productId, int attributeId);

    ProductAttributeValueResponse createValue(int productId, int attributeId, ProductAttributeValueRequest request);

    ProductAttributeValueResponse updateValue(int productId, int attributeId, int valueId,
                                              ProductAttributeValueRequest request);

    void deleteValue(int productId, int attributeId, int valueId);

    List<ProductAttributeValueImageResponse> addImages(int productId, int attributeId, int valueId,
                                                       List<MultipartFile> files);

    ProductAttributeValueImageResponse setPrimaryImage(int productId, int attributeId, int valueId, int imageId);

    void deleteImage(int productId, int attributeId, int valueId, int imageId);
}
