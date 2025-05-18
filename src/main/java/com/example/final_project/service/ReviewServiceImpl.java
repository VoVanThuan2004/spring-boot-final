package com.example.final_project.service;

import com.example.final_project.controller.ReviewSocketController;
import com.example.final_project.dto.ReviewRequest;
import com.example.final_project.dto.ReviewResponse;
import com.example.final_project.entity.Product;
import com.example.final_project.entity.Review;
import com.example.final_project.repository.ProductRepository;
import com.example.final_project.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewSocketController reviewSocketController;

    @Override
    public ResponseEntity<?> postReview(ReviewRequest reviewRequest) {
        Optional<Product> product = productRepository.findById(reviewRequest.getProductId());
        if (product.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        Review review = Review.builder()
                .product(product.get())
                .userName(reviewRequest.getUserName())
                .content(reviewRequest.getContent())
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .build();

        review = reviewRepository.save(review);

        // Gọi websocket lưu content review
        reviewSocketController.broadcastNewReview(review);

        return ResponseEntity.ok().body(review);
    }

    @Override
    public ResponseEntity<?> getReviewsByProductId(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        List<Review> reviewList = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);

        List<ReviewResponse> reviewResponseList = reviewList.stream()
                .map(review -> ReviewResponse.builder()
                        .id(review.getId())
                        .userName(review.getUserName())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(reviewResponseList);
    }
}
