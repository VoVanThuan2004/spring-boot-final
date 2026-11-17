package com.example.final_project.service;

import com.example.final_project.dto.request.InventoryRequest;
import com.example.final_project.dto.request.ProductVariantRequest;
import com.example.final_project.dto.response.ProductResponse;
import com.example.final_project.dto.response.ProductVariantResponse;
import com.example.final_project.entity.Inventory;
import com.example.final_project.entity.Product;
import com.example.final_project.entity.ProductVariant;
import com.example.final_project.enums.VariantStatus;
import com.example.final_project.mapper.ProductMapper;
import com.example.final_project.repository.InventoryRepository;
import com.example.final_project.repository.ProductRepository;
import com.example.final_project.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductImageResolver productImageResolver;
    private final ProductMapper productMapper;

    @Override
    public ResponseEntity<String> createProductVariant(ProductVariantRequest productVariantRequest, List<MultipartFile> files) {
        Optional<Product> product = productRepository.findById(productVariantRequest.getProductId());
        if (product.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        BigDecimal price = resolvePrice(productVariantRequest);

        ProductVariant productVariant = ProductVariant.builder()
                .sku(resolveSku(productVariantRequest))
                .name(productVariantRequest.getVariantName())
                .price(price)
                .compareAtPrice(productVariantRequest.getCompareAtPrice())
                .costPrice(productVariantRequest.getCostPrice())
                .status(VariantStatus.ACTIVE)
                .product(product.get())
                .build();

        productVariant = productVariantRepository.save(productVariant);

        Inventory inventory = Inventory.builder()
                .variant(productVariant)
                .quantity(productVariantRequest.getQuantity())
                .reservedQuantity(0)
                .lowStockThreshold(0)
                .build();

        inventoryRepository.save(inventory);
        return ResponseEntity.ok().body("Product variant created");
    }

    @Override
    public ResponseEntity<String> updateQuantityProductVariant(InventoryRequest inventoryRequest) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(inventoryRequest.getProductId());
        if (productVariant.isEmpty()) {
            return ResponseEntity.badRequest().body("ProductVariant not found");
        }

        Optional<Inventory> inventory = inventoryRepository.findByVariantId(productVariant.get().getId());
        if (inventory.isEmpty()) {
            return ResponseEntity.badRequest().body("Inventory not found");
        }

        if (inventoryRequest.getQuantity() < inventory.get().getReservedQuantity()) {
            return ResponseEntity.badRequest().body("Quantity cannot be less than reserved quantity");
        }

        inventory.get().setQuantity(inventoryRequest.getQuantity());
        inventoryRepository.save(inventory.get());
        return ResponseEntity.ok().body("Product variant updated");
    }

    @Override
    public ResponseEntity<?> getProductVariants(int productId) {
        List<ProductVariant> productVariantList = productVariantRepository.findByProductId(productId);
        if (productVariantList.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        List<ProductVariantResponse> productVariantResponses = productVariantList.stream()
                .map(productVariant -> {
                    Inventory inventory = productVariant.getInventory();
                    BigDecimal price = productVariant.getPrice() == null ? BigDecimal.ZERO : productVariant.getPrice();

                    return ProductVariantResponse.builder()
                            .id(productVariant.getId())
                            .sku(productVariant.getSku())
                            .variantName(productVariant.getName())
                            .name(productVariant.getName())
                            .price(price)
                            .compareAtPrice(productVariant.getCompareAtPrice())
                            .costPrice(productVariant.getCostPrice())
                            .priceDiff(price.doubleValue())
                            .status(productVariant.getStatus())
                            .quantity(inventory == null ? 0 : inventory.getQuantity())
                            .reservedQuantity(inventory == null ? 0 : inventory.getReservedQuantity())
                            .availableQuantity(inventory == null ? 0 : inventory.getAvailableQuantity())
                            .createdAt(toLocalDateTime(productVariant))
                            .discountPercent(discountPercent(productVariant))
                            .images(productImageResolver.getImages(productVariant))
                            .build();
                })
                .toList();
        return ResponseEntity.ok().body(productVariantResponses);
    }

    @Override
    public ResponseEntity<String> deleteProductVariant(int productVariant_id) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(productVariant_id);
        if (productVariant.isEmpty()) {
            return ResponseEntity.badRequest().body("Product variant not found");
        }

        productVariantRepository.delete(productVariant.get());
        return ResponseEntity.ok().body("Product variant deleted");
    }

    @Override
    public ResponseEntity<String> updateProductVariant(int productVariant_id, ProductVariantRequest productVariantRequest, List<MultipartFile> files) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(productVariant_id);
        if (productVariant.isEmpty()) {
            return ResponseEntity.badRequest().body("Product Variant not found");
        }

        Optional<Product> product = productRepository.findById(productVariantRequest.getProductId());
        if (product.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        productVariant.get().setSku(resolveSku(productVariantRequest, productVariant.get().getSku()));
        productVariant.get().setName(productVariantRequest.getVariantName());
        productVariant.get().setPrice(resolvePrice(productVariantRequest));
        productVariant.get().setCompareAtPrice(productVariantRequest.getCompareAtPrice());
        productVariant.get().setCostPrice(productVariantRequest.getCostPrice());
        productVariant.get().setProduct(product.get());
        productVariantRepository.save(productVariant.get());

        Optional<Inventory> inventory = inventoryRepository.findByVariantId(productVariant_id);
        if (inventory.isEmpty()) {
            return ResponseEntity.badRequest().body("Inventory not found");
        }

        if (productVariantRequest.getQuantity() < inventory.get().getReservedQuantity()) {
            return ResponseEntity.badRequest().body("Quantity cannot be less than reserved quantity");
        }

        inventory.get().setQuantity(productVariantRequest.getQuantity());
        inventoryRepository.save(inventory.get());

        return ResponseEntity.ok().body("Product variant updated");
    }

    @Override
    public ResponseEntity<String> setDiscountProductVariant(int productVariantId, double discountPercent) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(productVariantId);
        if (productVariant.isEmpty()) {
            return ResponseEntity.badRequest().body("Product variant not found");
        }

        if (discountPercent < 0 || discountPercent > 1.0) {
            return ResponseEntity.badRequest().body("Discount percent must be between 0 and 1");
        }

        BigDecimal basePrice = productVariant.get().getCompareAtPrice() != null
                ? productVariant.get().getCompareAtPrice()
                : productVariant.get().getPrice();

        if (basePrice == null) {
            return ResponseEntity.badRequest().body("Product variant has no price");
        }

        BigDecimal discountedPrice = basePrice.multiply(BigDecimal.valueOf(1 - discountPercent))
                .setScale(2, RoundingMode.HALF_UP);

        productVariant.get().setCompareAtPrice(basePrice);
        productVariant.get().setPrice(discountedPrice);
        productVariantRepository.save(productVariant.get());

        return ResponseEntity.ok().body("Product variant updated");
    }

    @Override
    public List<ProductResponse> getListProductsDiscount() {
        return productMapper.mapToResponseList(productRepository.findByDiscountPercent());
    }

    private BigDecimal resolvePrice(ProductVariantRequest request) {
        if (request.getPrice() != null) {
            return request.getPrice();
        }
        return BigDecimal.valueOf(Math.max(request.getPriceDiff(), 0));
    }

    private String resolveSku(ProductVariantRequest request) {
        return resolveSku(request, null);
    }

    private String resolveSku(ProductVariantRequest request, String currentSku) {
        if (request.getSku() != null && !request.getSku().isBlank()) {
            return request.getSku();
        }
        if (currentSku != null && !currentSku.isBlank()) {
            return currentSku;
        }
        return "SKU-" + request.getProductId() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private LocalDateTime toLocalDateTime(ProductVariant productVariant) {
        return productVariant.getCreatedAt() == null
                ? null
                : LocalDateTime.ofInstant(productVariant.getCreatedAt(), ZoneId.systemDefault());
    }

    private double discountPercent(ProductVariant productVariant) {
        if (productVariant.getCompareAtPrice() == null || productVariant.getPrice() == null
                || productVariant.getCompareAtPrice().compareTo(productVariant.getPrice()) <= 0) {
            return 0;
        }
        return productVariant.getCompareAtPrice()
                .subtract(productVariant.getPrice())
                .divide(productVariant.getCompareAtPrice(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
