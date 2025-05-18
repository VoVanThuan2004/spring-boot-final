package com.example.final_project.service;

import com.example.final_project.dto.RatingRequest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface RatingService {
    ResponseEntity<?> rateStarsProduct(RatingRequest ratingRequest, Principal principal);

    ResponseEntity<?> getRatingProduct(int productId);
}
