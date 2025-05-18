package com.example.final_project.service;

import com.example.final_project.cloudinary.CloudinaryService;
import com.example.final_project.dto.ProductRequest;
import com.example.final_project.dto.ProductResponse;
import com.example.final_project.entity.Brand;
import com.example.final_project.entity.Category;
import com.example.final_project.entity.Product;
import com.example.final_project.repository.BrandRepository;
import com.example.final_project.repository.CategoryRepository;
import com.example.final_project.repository.OrderItemRepository;
import com.example.final_project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CloudinaryService cloudinaryService;
    private final OrderItemRepository orderItemRepository;

    @Override
    public ResponseEntity<String> addProduct(ProductRequest productRequest, MultipartFile file) {
        if (productRequest == null) {
            return ResponseEntity.badRequest().body("Please provide a valid product");
        }

        // Check category
        Optional<Category> category = categoryRepository.findById(productRequest.getCategory_id());
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body("Category not found");
        }

        // Check brand
        Optional<Brand> brand = brandRepository.findById(productRequest.getBrand_id());
        if (brand.isEmpty()) {
            return ResponseEntity.badRequest().body("Brand not found");
        }

        // check file image
        String url_image = "";
        try {
            url_image = cloudinaryService.uploadImage(file);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Image cannot be uploaded");
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .specs(productRequest.getSpecs())
                .description(productRequest.getDescription())
                .image(url_image)
                .brand(brand.get())
                .category(category.get())
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .build();

        productRepository.save(product);
        return ResponseEntity.ok().body("Product added successfully!");
    }

    @Override
    public Page<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productRepository.findAll(pageable);


        return products.map(this::mapToResponse);
    }

    @Override
    public Page<ProductResponse> getFilterProducts(String keyword, List<Integer> categoryIds, List<Integer> brandIds, double minPrice, double maxPrice, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = Specification.where(null);

        if (!keyword.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
        }

        if (brandIds != null && !brandIds.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("brand").get("id").in(brandIds));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("category").get("id").in(categoryIds));
        }

        spec = spec.and((root, query, cb) ->
                cb.between(root.get("price"), minPrice, maxPrice));

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(this::mapToResponse);
    }

    @Override
    public List<ProductResponse> getAllProductsDefault() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .image(product.getImage())
                        .description(product.getDescription())
                        .specs(product.getSpecs())
                        .categoryName(product.getCategory().getName())
                        .brandName(product.getBrand().getName())
                        .createdAt(product.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public ResponseEntity<String> updateProduct(int productId, ProductRequest productRequest, MultipartFile file) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        // Lấy category, brand
        Optional<Category> category = categoryRepository.findById(productRequest.getCategory_id());
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body("Category not found");
        }

        Optional<Brand> brand = brandRepository.findById(productRequest.getBrand_id());
        if (brand.isEmpty()) {
            return ResponseEntity.badRequest().body("Brand not found");
        }

        product.get().setName(productRequest.getName());
        product.get().setPrice(productRequest.getPrice());
        product.get().setDescription(productRequest.getDescription());
        product.get().setSpecs(productRequest.getSpecs());
        product.get().setCategory(category.get());
        product.get().setBrand(brand.get());

        // check file image
        if (file != null) {
            String url_image = "";
            try {
                url_image = cloudinaryService.uploadImage(file);
                product.get().setImage(url_image);
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Image cannot be uploaded");
            }
        }

        productRepository.save(product.get());
        return ResponseEntity.ok().body("Product updated successfully!");
    }

    @Override
    public ResponseEntity<String> deleteProduct(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        productRepository.delete(product.get());
        return ResponseEntity.ok().body("Product deleted successfully!");
    }


    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .image(product.getImage())
                .description(product.getDescription())
                .specs(product.getSpecs())
                .categoryName(product.getCategory().getName())
                .brandName(product.getBrand().getName())
                .build();
    }

    @Override
    public List<ProductResponse> getNewProducts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);

        List<Product> products = productRepository.findProductsCreatedThisMonth(startOfMonth, endOfMonth);

        return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .image(product.getImage())
                        .description(product.getDescription())
                        .specs(product.getSpecs())
                        .categoryName(product.getCategory().getName())
                        .brandName(product.getBrand().getName())
                        .createdAt(product.getCreatedAt())
                        .discountPercent(product.getDiscountPercent())
                        .build())
                .toList();
    }

    @Override
    public List<ProductResponse> getAllProductsBestSeller() {
        int limit = 10;
        List<Product> products = orderItemRepository.getAllProductsBestSeller(PageRequest.of(0, limit));

        return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .image(product.getImage())
                        .specs(product.getSpecs())
                        .categoryName(product.getCategory().getName())
                        .brandName(product.getBrand().getName())
                        .createdAt(product.getCreatedAt())
                        .discountPercent(product.getDiscountPercent())
                        .build())
                .toList();
    }

}
