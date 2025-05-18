package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
public class RatingResponse {
    private int id;
    private int productId;
    private String userName;
    private int stars;
    private LocalDateTime createdAt;
}
