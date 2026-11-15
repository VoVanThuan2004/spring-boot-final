package com.example.final_project.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class ReviewRequest {
    private int productId;
    private String userName;
    private String content;
}
