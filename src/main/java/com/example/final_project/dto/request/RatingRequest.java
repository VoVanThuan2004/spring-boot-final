package com.example.final_project.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class RatingRequest {
    private int productId;
    private int stars;
}
