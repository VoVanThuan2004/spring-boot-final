package com.example.final_project.service;

import com.example.final_project.cloudinary.CloudinaryService;
import com.example.final_project.dto.request.ProductFilterRequest;
import com.example.final_project.dto.request.ProductRequest;
import com.example.final_project.dto.response.ProductResponse;
import com.example.final_project.dto.response.UploadResult;
import com.example.final_project.entity.Brand;
import com.example.final_project.entity.Category;
import com.example.final_project.entity.Product;
import com.example.final_project.enums.ProductStatus;
import com.example.final_project.exception.BadRequestException;
import com.example.final_project.exception.ResourceNotFoundException;
import com.example.final_project.mapper.ProductMapper;
import com.example.final_project.repository.BrandRepository;
import com.example.final_project.repository.CategoryRepository;
import com.example.final_project.repository.OrderItemRepository;
import com.example.final_project.repository.ProductRepository;
import com.example.final_project.util.ImageValidator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "name", "price", "slug", "createdAt", "updatedAt", "status"
    );

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final ImageValidator imageValidator;

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest productRequest, MultipartFile file) {
        if (productRequest == null) {
            throw new BadRequestException("Please provide a valid product");
        }

        Optional<Category> category = categoryRepository.findByIdAndIsDeletedFalse(productRequest.getCategoryId());
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category not found");
        }

        Optional<Brand> brand = brandRepository.findByIdAndIsDeletedFalse(productRequest.getBrandId());
        if (brand.isEmpty()) {
            throw new ResourceNotFoundException("Brand not found");
        }

        // Validate ảnh trước khi upload
        imageValidator.validate(file);

        // Upload ảnh
        String imageUrl = "";
        String publicId = "";
        if (file != null && !file.isEmpty()) {
            UploadResult upload = cloudinaryService.uploadFile(file);
            imageUrl = upload.getSecureUrl();
            publicId = upload.getPublicId();
            registerImageRollback(upload.getPublicId());
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .slug(generateSlug(productRequest.getName()))
                .specifications(productRequest.getSpecs())
                .description(productRequest.getDescription())
                .brand(brand.get())
                .category(category.get())
                .status(ProductStatus.ACTIVE)
                .imageUrl(imageUrl)
                .publicId(publicId)
                .build();

        productRepository.save(product);

        return productMapper.mapToResponse(product);
    }

    /**
     * Nếu transaction bị rollback (ví dụ save DB lỗi, mapper lỗi, ...),
     * tự động xóa ảnh đã upload trên Cloudinary.
     */
    private void registerImageRollback(String publicId) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            try {
                                cloudinaryService.deleteFile(publicId);
                            } catch (Exception e) {
                                // Không throw ở đây — transaction đã kết thúc rồi
//                                log.error("Failed to cleanup cloudinary image {}", publicId, e);
                            }
                        }
                    }
                }
        );
    }

    private void registerImageCleanupAfterCommit(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            cloudinaryService.deleteFile(publicId);
                        } catch (Exception e) {
//                            log.error("Failed to cleanup old cloudinary image {}", publicId, e);
                        }
                    }
                }
        );
    }

    private Product findProduct(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    @Override
    public Page<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productRepository.findAll(pageable);

        return products.map(productMapper::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getFilterProducts(ProductFilterRequest request) {
        validateFilter(request);

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                buildSort(request.getSortBy(), request.getDirection())
        );

        return productRepository.findAll(buildFilterSpecification(request), pageable)
                .map(productMapper::mapToResponse);
    }

    @Override
    public List<ProductResponse> getAllProductsDefault() {
        return productMapper.mapToResponseList(productRepository.findAll());
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(int productId, ProductRequest productRequest, MultipartFile file) {
        if (productRequest == null) {
            throw new BadRequestException("Please provide a valid product");
        }

        Product product = findProduct(productId);

        if (product.getStatus() == ProductStatus.ARCHIVED) {
            throw new BadRequestException("Product is archived and cannot be updated");
        }

        Optional<Category> category = categoryRepository.findByIdAndIsDeletedFalse(productRequest.getCategoryId());
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category not found");
        }

        Optional<Brand> brand = brandRepository.findByIdAndIsDeletedFalse(productRequest.getBrandId());
        if (brand.isEmpty()) {
            throw new ResourceNotFoundException("Brand not found");
        }

        imageValidator.validate(file);

        if (file != null && !file.isEmpty()) {
            String oldPublicId = product.getPublicId();
            UploadResult upload = cloudinaryService.uploadFile(file);

            product.setImageUrl(upload.getSecureUrl());
            product.setPublicId(upload.getPublicId());

            registerImageRollback(upload.getPublicId());
            registerImageCleanupAfterCommit(oldPublicId);
        }

        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setSpecifications(productRequest.getSpecs());
        product.setCategory(category.get());
        product.setBrand(brand.get());

        productRepository.save(product);
        return productMapper.mapToResponse(product);
    }

    @Override
    @Transactional
    public boolean softDeleteProduct(int productId) {
        Product product = findProduct(productId);

        if (product.getStatus() == ProductStatus.ARCHIVED) {
            return false;
        }

        product.setStatus(ProductStatus.ARCHIVED);
        productRepository.save(product);
        return true;
    }

    @Override
    @Transactional
    public ProductResponse restoreProduct(int productId) {
        Product product = findProduct(productId);

        if (product.getStatus() != ProductStatus.ARCHIVED) {
            throw new BadRequestException("Product is not archived");
        }

        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
        return productMapper.mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getNewProducts() {
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDate today = LocalDate.now(zone);
        Instant startOfMonth = today.withDayOfMonth(1).atStartOfDay(zone).toInstant();
        Instant endOfMonth = today.withDayOfMonth(today.lengthOfMonth())
                .atTime(LocalTime.MAX).atZone(zone).toInstant();

        return productMapper.mapToResponseList(
                productRepository.findProductsCreatedThisMonth(startOfMonth, endOfMonth));
    }

    @Override
    public List<ProductResponse> getAllProductsBestSeller() {
        int limit = 10;
        return productMapper.mapToResponseList(
                orderItemRepository.getAllProductsBestSeller(PageRequest.of(0, limit)));
    }

    private Specification<Product> buildFilterSpecification(ProductFilterRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getKeyword())) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + request.getKeyword().trim().toLowerCase() + "%"
                ));
            }

            if (request.getBrandIds() != null && !request.getBrandIds().isEmpty()) {
                predicates.add(root.get("brand").get("id").in(request.getBrandIds()));
            }

            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                predicates.add(root.get("category").get("id").in(request.getCategoryIds()));
            }

            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }

            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Sort buildSort(String sortBy, String direction) {
        String field = StringUtils.hasText(sortBy) ? sortBy.trim() : "name";
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new BadRequestException(
                    "Invalid sortBy '" + field + "'. Allowed values: "
                            + String.join(", ", ALLOWED_SORT_FIELDS)
            );
        }

        Sort.Direction sortDirection;
        if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new BadRequestException("Invalid direction '" + direction + "'. Allowed values: asc, desc");
        }

        return Sort.by(sortDirection, field);
    }

    private void validateFilter(ProductFilterRequest request) {
        if (request == null) {
            throw new BadRequestException("Filter request is required");
        }

        if (request.getPage() < 0) {
            throw new BadRequestException("page must be greater than or equal to 0");
        }

        if (request.getSize() < 1 || request.getSize() > 100) {
            throw new BadRequestException("size must be between 1 and 100");
        }

        BigDecimal minPrice = request.getMinPrice();
        BigDecimal maxPrice = request.getMaxPrice();

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("minPrice must be greater than or equal to 0");
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("maxPrice must be greater than or equal to 0");
        }

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException("minPrice must be less than or equal to maxPrice");
        }
    }

    private String generateSlug(String name) {
        String base = name == null ? "product" : name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
        if (base.isBlank()) {
            base = "product";
        }
        return base + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
