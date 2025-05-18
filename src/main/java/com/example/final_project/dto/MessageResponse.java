package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class MessageResponse {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String content;
    private String image;
    private LocalDateTime dateTime;
}
