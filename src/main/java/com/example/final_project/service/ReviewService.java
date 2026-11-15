package com.example.final_project.service;

import com.example.final_project.dto.request.ReviewRequest;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<?> postReview(ReviewRequest reviewRequest);

    ResponseEntity<?> getReviewsByProductId(int productId);
}
