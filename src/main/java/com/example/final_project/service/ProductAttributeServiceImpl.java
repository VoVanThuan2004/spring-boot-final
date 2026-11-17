package com.example.final_project.service;

import com.example.final_project.cloudinary.CloudinaryService;
import com.example.final_project.dto.request.ProductAttributeRequest;
import com.example.final_project.dto.request.ProductAttributeValueRequest;
import com.example.final_project.dto.response.ProductAttributeResponse;
import com.example.final_project.dto.response.ProductAttributeValueImageResponse;
import com.example.final_project.dto.response.ProductAttributeValueResponse;
import com.example.final_project.dto.response.UploadResult;
import com.example.final_project.entity.Product;
import com.example.final_project.entity.ProductAttribute;
import com.example.final_project.entity.ProductAttributeValue;
import com.example.final_project.entity.ProductAttributeValueImage;
import com.example.final_project.enums.ProductStatus;
import com.example.final_project.exception.BadRequestException;
import com.example.final_project.exception.ConflictException;
import com.example.final_project.exception.ResourceNotFoundException;
import com.example.final_project.mapper.ProductAttributeMapper;
import com.example.final_project.repository.ProductAttributeRepository;
import com.example.final_project.repository.ProductAttributeValueImageRepository;
import com.example.final_project.repository.ProductAttributeValueRepository;
import com.example.final_project.repository.ProductRepository;
import com.example.final_project.repository.VariantAttributeValueRepository;
import com.example.final_project.util.ImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private static final int MAX_IMAGES_PER_VALUE = 10;

    private final ProductRepository productRepository;
    private final ProductAttributeRepository attributeRepository;
    private final ProductAttributeValueRepository valueRepository;
    private final ProductAttributeValueImageRepository imageRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final ProductAttributeMapper attributeMapper;
    private final CloudinaryService cloudinaryService;
    private final ImageValidator imageValidator;

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeResponse> getAttributes(int productId) {
        findProduct(productId);
        return attributeRepository.findByProductIdOrderBySortOrderAsc(productId)
                .stream()
                .map(attributeMapper::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAttributeResponse getAttribute(int productId, int attributeId) {
        return attributeMapper.mapToResponse(findAttribute(productId, attributeId));
    }

    @Override
    public ProductAttributeResponse createAttribute(int productId, ProductAttributeRequest request) {
        Product product = findWritableProduct(productId);
        String name = request.getName().trim();

        List<ProductAttribute> attributes =
                attributeRepository.findByProductIdOrderBySortOrderAsc(productId);

        if (attributes.stream().anyMatch(item -> item.getName().equalsIgnoreCase(name))) {
            throw new BadRequestException("Attribute name already exists in this product");
        }

        boolean visual = Boolean.TRUE.equals(request.getIsVisual());
        if (visual && attributes.stream().anyMatch(ProductAttribute::isVisual)) {
            throw new BadRequestException("Product already has a visual attribute");
        }

        int sortOrder = request.getSortOrder() != null
                ? request.getSortOrder()
                : attributes.stream().mapToInt(ProductAttribute::getSortOrder).max().orElse(-1) + 1;

        ProductAttribute attribute = ProductAttribute.builder()
                .product(product)
                .name(name)
                .isVisual(visual)
                .sortOrder(sortOrder)
                .build();

        return attributeMapper.mapToResponse(attributeRepository.save(attribute));
    }

    @Override
    public ProductAttributeResponse updateAttribute(int productId, int attributeId, ProductAttributeRequest request) {
        findWritableProduct(productId);
        ProductAttribute attribute = findAttribute(productId, attributeId);
        String name = request.getName().trim();

        List<ProductAttribute> attributes =
                attributeRepository.findByProductIdOrderBySortOrderAsc(productId);

        boolean duplicated = attributes.stream()
                .anyMatch(item -> item.getId() != attributeId && item.getName().equalsIgnoreCase(name));
        if (duplicated) {
            throw new BadRequestException("Attribute name already exists in this product");
        }

        boolean visual = request.getIsVisual() != null ? request.getIsVisual() : attribute.isVisual();
        boolean anotherVisual = attributes.stream()
                .anyMatch(item -> item.getId() != attributeId && item.isVisual());
        if (visual && anotherVisual) {
            throw new BadRequestException("Product already has a visual attribute");
        }

        attribute.setName(name);
        attribute.setVisual(visual);
        if (request.getSortOrder() != null) {
            attribute.setSortOrder(request.getSortOrder());
        }

        return attributeMapper.mapToResponse(attributeRepository.save(attribute));
    }

    @Override
    public void deleteAttribute(int productId, int attributeId) {
        findWritableProduct(productId);
        ProductAttribute attribute = findAttribute(productId, attributeId);

        if (variantAttributeValueRepository.existsByAttributeId(attributeId)) {
            throw new ConflictException("Attribute is being used by product variants");
        }

        List<String> publicIds = collectImagePublicIds(attribute);
        attributeRepository.delete(attribute);
        registerImageCleanupAfterCommit(publicIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeValueResponse> getValues(int productId, int attributeId) {
        findAttribute(productId, attributeId);
        return valueRepository.findByAttributeIdOrderBySortOrderAsc(attributeId)
                .stream()
                .map(attributeMapper::mapToValueResponse)
                .toList();
    }

    @Override
    public ProductAttributeValueResponse createValue(int productId, int attributeId,
                                                     ProductAttributeValueRequest request) {
        findWritableProduct(productId);
        ProductAttribute attribute = findAttribute(productId, attributeId);
        String value = request.getValue().trim();

        if (valueRepository.findByAttributeIdAndValueIgnoreCase(attributeId, value).isPresent()) {
            throw new BadRequestException("Attribute value already exists");
        }

        int sortOrder = request.getSortOrder() != null
                ? request.getSortOrder()
                : nextValueSortOrder(attributeId);

        ProductAttributeValue attributeValue = ProductAttributeValue.builder()
                .attribute(attribute)
                .value(value)
                .sortOrder(sortOrder)
                .build();

        return attributeMapper.mapToValueResponse(valueRepository.save(attributeValue));
    }

    @Override
    public ProductAttributeValueResponse updateValue(int productId, int attributeId, int valueId,
                                                     ProductAttributeValueRequest request) {
        findWritableProduct(productId);
        findAttribute(productId, attributeId);
        ProductAttributeValue attributeValue = findValue(attributeId, valueId);
        String newValue = request.getValue().trim();

        Optional<ProductAttributeValue> duplicate =
                valueRepository.findByAttributeIdAndValueIgnoreCase(attributeId, newValue);
        if (duplicate.isPresent() && duplicate.get().getId() != valueId) {
            throw new BadRequestException("Attribute value already exists");
        }

        attributeValue.setValue(newValue);
        if (request.getSortOrder() != null) {
            attributeValue.setSortOrder(request.getSortOrder());
        }

        return attributeMapper.mapToValueResponse(valueRepository.save(attributeValue));
    }

    @Override
    public void deleteValue(int productId, int attributeId, int valueId) {
        findWritableProduct(productId);
        findAttribute(productId, attributeId);
        ProductAttributeValue value = findValue(attributeId, valueId);

        if (variantAttributeValueRepository.existsByAttributeValueId(valueId)) {
            throw new ConflictException("Attribute value is being used by product variants");
        }

        List<String> publicIds = collectImagePublicIds(value);
        valueRepository.delete(value);
        registerImageCleanupAfterCommit(publicIds);
    }

    @Override
    public List<ProductAttributeValueImageResponse> addImages(int productId, int attributeId, int valueId,
                                                              List<MultipartFile> files) {
        findWritableProduct(productId);
        findAttribute(productId, attributeId);
        ProductAttributeValue value = findValue(attributeId, valueId);

        List<MultipartFile> validFiles = files == null
                ? List.of()
                : files.stream().filter(file -> file != null && !file.isEmpty()).toList();

        if (validFiles.isEmpty()) {
            throw new BadRequestException("At least one image is required");
        }

        List<ProductAttributeValueImage> existing =
                imageRepository.findByAttributeValueIdOrderBySortOrderAsc(valueId);

        if (existing.size() + validFiles.size() > MAX_IMAGES_PER_VALUE) {
            throw new BadRequestException("A value can have at most " + MAX_IMAGES_PER_VALUE + " images");
        }

        validFiles.forEach(imageValidator::validate);

        boolean hasPrimary = existing.stream().anyMatch(ProductAttributeValueImage::isPrimary);
        int sortOrder = existing.stream()
                .mapToInt(ProductAttributeValueImage::getSortOrder)
                .max()
                .orElse(-1) + 1;

        List<ProductAttributeValueImage> saved = new ArrayList<>();
        for (int i = 0; i < validFiles.size(); i++) {
            UploadResult upload = cloudinaryService.uploadFile(validFiles.get(i));
            registerImageRollback(upload.getPublicId());

            ProductAttributeValueImage image = ProductAttributeValueImage.builder()
                    .attributeValue(value)
                    .imageUrl(upload.getSecureUrl())
                    .cloudinaryPublicId(upload.getPublicId())
                    .sortOrder(sortOrder++)
                    .isPrimary(!hasPrimary && i == 0)
                    .build();

            saved.add(imageRepository.save(image));
        }

        return saved.stream().map(attributeMapper::mapToImageResponse).toList();
    }

    @Override
    public ProductAttributeValueImageResponse setPrimaryImage(int productId, int attributeId, int valueId,
                                                              int imageId) {
        findWritableProduct(productId);
        findAttribute(productId, attributeId);
        findValue(attributeId, valueId);

        ProductAttributeValueImage target = imageRepository.findByIdAndAttributeValueId(imageId, valueId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));

        List<ProductAttributeValueImage> images =
                imageRepository.findByAttributeValueIdOrderBySortOrderAsc(valueId);

        for (ProductAttributeValueImage image : images) {
            image.setPrimary(image.getId() == imageId);
        }

        imageRepository.saveAll(images);
        return attributeMapper.mapToImageResponse(target);
    }

    @Override
    public void deleteImage(int productId, int attributeId, int valueId, int imageId) {
        findWritableProduct(productId);
        findAttribute(productId, attributeId);
        findValue(attributeId, valueId);

        ProductAttributeValueImage image = imageRepository.findByIdAndAttributeValueId(imageId, valueId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));

        List<ProductAttributeValueImage> images =
                imageRepository.findByAttributeValueIdOrderBySortOrderAsc(valueId);

        Optional<ProductAttributeValueImage> nextPrimary = images.stream()
                .filter(item -> item.getId() != imageId)
                .min(Comparator.comparingInt(ProductAttributeValueImage::getSortOrder)
                        .thenComparingInt(ProductAttributeValueImage::getId));

        boolean wasPrimary = image.isPrimary();
        imageRepository.delete(image);

        if (wasPrimary && nextPrimary.isPresent()) {
            nextPrimary.get().setPrimary(true);
            imageRepository.save(nextPrimary.get());
        }

        List<String> publicIds = new ArrayList<>();
        if (StringUtils.hasText(image.getCloudinaryPublicId())) {
            publicIds.add(image.getCloudinaryPublicId());
        }
        registerImageCleanupAfterCommit(publicIds);
    }

    private Product findProduct(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    private Product findWritableProduct(int productId) {
        Product product = findProduct(productId);
        if (product.getStatus() == ProductStatus.ARCHIVED) {
            throw new BadRequestException("Product is archived and cannot be updated");
        }
        return product;
    }

    private ProductAttribute findAttribute(int productId, int attributeId) {
        return attributeRepository.findByIdAndProductId(attributeId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAttribute", "id", attributeId));
    }

    private ProductAttributeValue findValue(int attributeId, int valueId) {
        return valueRepository.findByIdAndAttributeId(valueId, attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAttributeValue", "id", valueId));
    }

    private int nextValueSortOrder(int attributeId) {
        return valueRepository.findByAttributeIdOrderBySortOrderAsc(attributeId)
                .stream()
                .mapToInt(ProductAttributeValue::getSortOrder)
                .max()
                .orElse(-1) + 1;
    }

    private List<String> collectImagePublicIds(ProductAttribute attribute) {
        List<String> publicIds = new ArrayList<>();
        if (attribute.getValues() == null) {
            return publicIds;
        }
        for (ProductAttributeValue value : attribute.getValues()) {
            publicIds.addAll(collectImagePublicIds(value));
        }
        return publicIds.stream().distinct().toList();
    }

    private List<String> collectImagePublicIds(ProductAttributeValue value) {
        List<String> publicIds = new ArrayList<>();
        if (value.getImages() == null) {
            return publicIds;
        }
        for (ProductAttributeValueImage image : value.getImages()) {
            if (StringUtils.hasText(image.getCloudinaryPublicId())) {
                publicIds.add(image.getCloudinaryPublicId());
            }
        }
        return publicIds.stream().distinct().toList();
    }

    private void registerImageRollback(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    try {
                        cloudinaryService.deleteFile(publicId);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    private void registerImageCleanupAfterCommit(List<String> publicIds) {
        List<String> ids = publicIds.stream()
                .filter(StringUtils::hasText)
                .distinct()
                .toList();

        if (ids.isEmpty()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (String publicId : ids) {
                    try {
                        cloudinaryService.deleteFile(publicId);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }
}
