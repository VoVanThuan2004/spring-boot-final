package com.example.final_project.controller;

import com.example.final_project.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastMessage(MessageResponse messageResponse) {
        messagingTemplate.convertAndSend("/topic/message/" + messageResponse.getReceiverId(), messageResponse);
    }
}
