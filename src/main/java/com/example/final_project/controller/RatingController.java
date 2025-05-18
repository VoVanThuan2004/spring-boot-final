package com.example.final_project.controller;

import com.example.final_project.dto.RatingRequest;
import com.example.final_project.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/api/v1/ratings")
    public ResponseEntity<?> rateStarsProduct(@RequestBody RatingRequest ratingRequest, Principal principal) {
        return ratingService.rateStarsProduct(ratingRequest, principal);
    }

    // Lấy list ratings dựa theo productId
    @GetMapping("/api/v1/ratings/{product_id}")
    public ResponseEntity<?> getRatingProduct(@PathVariable("product_id") int product_id) {
        return ratingService.getRatingProduct(product_id);
    }

}
