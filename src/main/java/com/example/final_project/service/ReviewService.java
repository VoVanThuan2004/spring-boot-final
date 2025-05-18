package com.example.final_project.service;

import com.example.final_project.dto.ReviewRequest;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<?> postReview(ReviewRequest reviewRequest);

    ResponseEntity<?> getReviewsByProductId(int productId);
}
