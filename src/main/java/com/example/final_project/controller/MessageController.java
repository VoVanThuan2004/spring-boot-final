package com.example.final_project.controller;

import com.example.final_project.dto.AdminMessageRequest;
import com.example.final_project.dto.CustomUserDetails;
import com.example.final_project.dto.MessageRequest;
import com.example.final_project.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5000")
public class MessageController {
    private final MessageService messageService;

    // API gửi tin nhắn từ user gửi cho admin
    @PostMapping(value = "/api/v1/message/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendMessage(
        @RequestPart("message") MessageRequest messageRequest,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getId();
        return messageService.sendMessage(messageRequest, image, userId);
    }

    // API gửi tin nhắn từ admin -> user
    @PostMapping(value = "/api/v1/message/reply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> replyMessage(
            @RequestPart("message") AdminMessageRequest messageRequest,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int adminId = userDetails.getId();
        return messageService.replyMessage(messageRequest, image, adminId);
    }

    // API tra ve noi dung cuoc tro chuyen
    @GetMapping("/api/v1/message/conversation")
    public ResponseEntity<?> getConversation(
            @RequestParam("withUserId") int withUserId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int currentUserId = userDetails.getId();
        return messageService.getConversation(currentUserId, withUserId);
    }

    // API lấy danh sách user đã trò chuyện với admin
    @GetMapping("/api/v1/message/users")
    public ResponseEntity<?> getUsersChattedWith() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int adminId = userDetails.getId();
        return messageService.getUsersChattedWith(adminId);
    }
}
