package com.example.final_project.controller;

import com.example.final_project.dto.ReviewRequest;
import com.example.final_project.repository.ProductRepository;
import com.example.final_project.repository.ReviewRepository;
import com.example.final_project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/v1/reviews")
    public ResponseEntity<?> postReview(@RequestBody ReviewRequest reviewRequest) {
        return reviewService.postReview(reviewRequest);
    }

    // Lấy list reviews theo productId
    @GetMapping("/api/v1/reviews/{product_id}")
    public ResponseEntity<?> getReviewsByProductId(@PathVariable int product_id) {
        return reviewService.getReviewsByProductId(product_id);
    }
}
