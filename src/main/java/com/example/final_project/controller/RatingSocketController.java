package com.example.final_project.controller;

import com.example.final_project.dto.RatingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RatingSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastNewRating(RatingResponse ratingResponse) {
        messagingTemplate.convertAndSend("/topic/ratings/" + ratingResponse.getProductId(), ratingResponse);
    }
}
