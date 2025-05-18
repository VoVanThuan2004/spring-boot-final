package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class AdminMessageRequest {
    private int receiverId;
    private String content;
}
