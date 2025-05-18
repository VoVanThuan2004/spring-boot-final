package com.example.final_project.controller;

import com.example.final_project.dto.ReviewResponse;
import com.example.final_project.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastNewReview(Review review) {
        ReviewResponse response = new ReviewResponse(review);
        messagingTemplate.convertAndSend(
                "/topic/reviews/" + review.getProduct().getId(), response
        );
    }
}
