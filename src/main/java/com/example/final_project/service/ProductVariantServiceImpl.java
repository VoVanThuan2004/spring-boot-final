package com.example.final_project.service;

import com.example.final_project.cloudinary.CloudinaryService;
import com.example.final_project.dto.InventoryRequest;
import com.example.final_project.dto.ProductResponse;
import com.example.final_project.dto.ProductVariantRequest;
import com.example.final_project.dto.ProductVariantResponse;
import com.example.final_project.entity.Images;
import com.example.final_project.entity.Inventory;
import com.example.final_project.entity.Product;
import com.example.final_project.entity.ProductVariant;
import com.example.final_project.repository.ImagesRepository;
import com.example.final_project.repository.InventoryRepository;
import com.example.final_project.repository.ProductRepository;
import com.example.final_project.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final CloudinaryService cloudinaryService;
    private final ImagesRepository imagesRepository;

    @Override
    public ResponseEntity<String> createProductVariant(ProductVariantRequest productVariantRequest, List<MultipartFile> files) {
        Optional<Product> product = productRepository.findById(productVariantRequest.getProductId());
        if (product.isPresent()) {
            ProductVariant productVariant = ProductVariant.builder()
                    .specs(productVariantRequest.getSpecs())
                    .variantName(productVariantRequest.getVariantName())
                    .costPrice(productVariantRequest.getCostPrice())
                    .priceDiff(productVariantRequest.getPriceDiff())
                    .product(product.get())
                    .createdAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                    .discountPercent(1)
                    .build();

            // Lưu images
            List<Images> images = new ArrayList<>();
            for (MultipartFile file : files) {
                Images image = new Images();
                String urlImage = "";
                try {
                    urlImage = cloudinaryService.uploadImage(file);
                    image.setImagePath(urlImage);
                    image.setProductVariant(productVariant);
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body("Image can not be uploaded");
                }
                images.add(image);
            }

            // Save variant before inventory
            productVariant = productVariantRepository.save(productVariant);

            Inventory inventory = Inventory.builder()
                    .productVariant(productVariant)
                    .quantity(productVariantRequest.getQuantity())
                    .build();

            inventoryRepository.save(inventory);

            // Save images
            imagesRepository.saveAll(images);

            return ResponseEntity.ok().body("Product variant created");
        }
        return ResponseEntity.badRequest().body("Product not found");
    }

    @Override
    public ResponseEntity<String> updateQuantityProductVariant(InventoryRequest inventoryRequest) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(inventoryRequest.getProductId());
        Optional<Inventory> inventory = inventoryRepository.findByProductVariantId(productVariant.get().getId());
        if (inventory.isPresent()) {
            inventory.get().setQuantity(inventoryRequest.getQuantity());
            inventoryRepository.save(inventory.get());
            return ResponseEntity.ok().body("Product variant updated");
        }
        return ResponseEntity.badRequest().body("ProductVariant not found");
    }

    @Override
    public ResponseEntity<?> getProductVariants(int productId) {
        List<ProductVariant> productVariantList = productVariantRepository.findByProductId(productId);
        if (productVariantList.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        List<ProductVariantResponse> productVariantResponses = productVariantList.stream()
                .map(productVariant -> ProductVariantResponse.builder()
                        .id(productVariant.getId())
                        .variantName(productVariant.getVariantName())
                        .priceDiff(productVariant.getPriceDiff())
                        .costPrice(productVariant.getCostPrice())
                        .specs(productVariant.getSpecs())
                        .quantity(productVariant.getInventory().getQuantity())
                        .createdAt(productVariant.getCreatedAt())
                        .discountPercent(productVariant.getDiscountPercent())
                        .images(productVariant.getImages().stream()
                                .map(Images::getImagePath)
                                .toList())
                        .build())
                .toList();
        return ResponseEntity.ok().body(productVariantResponses);
    }

    @Override
    public ResponseEntity<String> deleteProductVariant(int productVariant_id) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(productVariant_id);
        if (productVariant.isPresent()) {
            productVariantRepository.delete(productVariant.get());

            // Sau khi xóa variant -> xóa inventory của variant đó
            Optional<Inventory> inventory = inventoryRepository.findByProductVariantId(productVariant.get().getId());
            inventory.ifPresent(inventoryRepository::delete);
            return ResponseEntity.ok().body("Product variant deleted");
        }
        return ResponseEntity.badRequest().body("Product variant not found");
    }

    @Override
    public ResponseEntity<String> updateProductVariant(int productVariant_id, ProductVariantRequest productVariantRequest, List<MultipartFile> files) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(productVariant_id);
        if (productVariant.isEmpty()) {
            return ResponseEntity.badRequest().body("Product Variant not found");
        }

        // Kiểm tra product original
        Optional<Product> product = productRepository.findById(productVariantRequest.getProductId());
        if (product.isEmpty()){
            return ResponseEntity.badRequest().body("Product not found");
        }

        productVariant.get().setVariantName(productVariantRequest.getVariantName());
        productVariant.get().setCostPrice(productVariantRequest.getCostPrice());
        productVariant.get().setPriceDiff(productVariantRequest.getPriceDiff());
        productVariant.get().setSpecs(productVariantRequest.getSpecs());
        productVariant.get().setProduct(product.get());
        productVariant.get().setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        Optional<ProductVariant> productVariantSaved = Optional.of(productVariantRepository.save(productVariant.get()));

        // Lưu images
        // Update lại ảnh mới
        List<Images> images = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            List<Images> imagesList = imagesRepository.findAllByProductVariantId(productVariant.get().getId());
            // Xóa image cũ
            imagesRepository.deleteAll(imagesList);

            for (MultipartFile file : files) {
                Images image = new Images();
                String urlImage = "";
                try {
                    urlImage = cloudinaryService.uploadImage(file);
                    image.setImagePath(urlImage);
                    image.setProductVariant(productVariantSaved.get());
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body("Image can not be uploaded");
                }
                images.add(image);
            }
        }

        Optional<Inventory> inventory = inventoryRepository.findByProductVariantId(productVariant_id);
        if (inventory.isEmpty()) {
            return ResponseEntity.badRequest().body("Product variant not found");
        }

        inventory.get().setQuantity(productVariantRequest.getQuantity());
        inventoryRepository.save(inventory.get());

        // Lưu ảnh
        imagesRepository.saveAll(images);

        return ResponseEntity.ok().body("Product variant updated");
    }

    @Override
    public ResponseEntity<String> setDiscountProductVariant(int productVariantId, double discountPercent) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(productVariantId);
        if (productVariant.isEmpty()) {
            return ResponseEntity.badRequest().body("Product variant not found");
        }

        if (discountPercent > 0.5) {
            return ResponseEntity.badRequest().body("Discount percent must less than 0.5");
        }

        productVariant.get().getProduct().setDiscountPercent(discountPercent);
        productVariant.get().setDiscountPercent(discountPercent);
        productVariantRepository.save(productVariant.get());

        return ResponseEntity.ok().body("Product variant updated");
    }

    @Override
    public List<ProductResponse> getListProductsDiscount() {
        List<Product> products = productRepository.findByDiscountPercent();

        return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .image(product.getImage())
                        .description(product.getDescription())
                        .specs(product.getSpecs())
                        .createdAt(product.getCreatedAt())
                        .categoryName(product.getCategory().getName())
                        .brandName(product.getBrand().getName())
                        .discountPercent(product.getDiscountPercent())
                        .build())
                .toList();
    }
}
