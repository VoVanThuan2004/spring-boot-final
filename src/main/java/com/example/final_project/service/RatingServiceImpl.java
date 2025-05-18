package com.example.final_project.service;

import com.example.final_project.controller.RatingSocketController;
import com.example.final_project.dto.RatingRequest;
import com.example.final_project.dto.RatingResponse;
import com.example.final_project.entity.Product;
import com.example.final_project.entity.Rating;
import com.example.final_project.entity.User;
import com.example.final_project.repository.ProductRepository;
import com.example.final_project.repository.RatingRepository;
import com.example.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final RatingSocketController ratingSocketController;

    @Override
    public ResponseEntity<?> rateStarsProduct(RatingRequest ratingRequest, Principal principal) {
        String fullname = (String) principal.getName();
        Optional<User> user = userRepository.findByFullName(fullname);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Optional<Product> product = productRepository.findById(ratingRequest.getProductId());
        if (product.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        // Kiểm tra xem user đã từng đánh giá sản phẩm chưa
        Optional<Rating> rating = ratingRepository.findByUserIdAndProductId(user.get().getId(), product.get().getId());
        if (rating.isPresent()) {
            return ResponseEntity.badRequest().body("You have already rated this product.");
        }

        Rating savedRating = Rating.builder()
                .stars(ratingRequest.getStars())
                .product(product.get())
                .user(user.get())
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .build();

        savedRating = ratingRepository.save(savedRating);

        RatingResponse ratingResponse = RatingResponse.builder()
                .id(savedRating.getId())
                .productId(savedRating.getProduct().getId())
                .userName(savedRating.getUser().getFullName())
                .stars(savedRating.getStars())
                .createdAt(savedRating.getCreatedAt())
                        .build();

        // Gọi websocket lưu content review
        ratingSocketController.broadcastNewRating(ratingResponse);

        return ResponseEntity.ok().body(ratingResponse);
    }

    @Override
    public ResponseEntity<?> getRatingProduct(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        List<Rating> ratingList = ratingRepository.findByProductId(productId);
        List<RatingResponse> ratingResponseList = ratingList.stream()
                .map(rating -> RatingResponse.builder()
                        .id(rating.getId())
                        .productId(rating.getProduct().getId())
                        .userName(rating.getUser().getFullName())
                        .stars(rating.getStars())
                        .createdAt(rating.getCreatedAt())
                        .build())
                .toList();
        return ResponseEntity.ok().body(ratingResponseList);
    }
}
