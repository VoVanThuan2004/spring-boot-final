package com.example.final_project.dto;

import com.example.final_project.entity.Review;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private int id;
    private String userName;
    private String content;
    private LocalDateTime createdAt;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.userName = review.getUserName();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();
    }
}
