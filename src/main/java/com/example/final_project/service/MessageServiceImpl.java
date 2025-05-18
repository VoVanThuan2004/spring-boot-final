package com.example.final_project.service;

import com.example.final_project.cloudinary.CloudinaryService;
import com.example.final_project.controller.MessageSocketController;
import com.example.final_project.dto.AdminMessageRequest;
import com.example.final_project.dto.MessageRequest;
import com.example.final_project.dto.MessageResponse;
import com.example.final_project.dto.UserResponse;
import com.example.final_project.entity.Message;
import com.example.final_project.entity.Role;
import com.example.final_project.entity.User;
import com.example.final_project.entity.UserRole;
import com.example.final_project.repository.MessageRepository;
import com.example.final_project.repository.RoleRepository;
import com.example.final_project.repository.UserRepository;
import com.example.final_project.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final MessageRepository messageRepository;
    private final MessageSocketController messageSocketController;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public ResponseEntity<?> sendMessage(MessageRequest messageRequest, MultipartFile image, int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Check tin nhắn có rỗng hay không
        if ((messageRequest.getContent() == null || messageRequest.getContent().isBlank()) && image == null) {
            return ResponseEntity.badRequest().body("Content or image must be provided");
        }

        String imageUrl = "";
        if (image != null) {
            try {
                imageUrl = cloudinaryService.uploadImage(image);
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Upload image failed");
            }
        }

        // admin_id = 2
        Optional<Role> role = roleRepository.findByName("ADMIN");
        if (role.isEmpty()) {
            return ResponseEntity.badRequest().body("Role not found");
        }
        Optional<UserRole> userRole = userRoleRepository.findByRoleId(role.get().getId());
        if (userRole.isEmpty()) {
            return ResponseEntity.badRequest().body("UserRole not found");
        }
        Optional<User> receiverAdmin = userRepository.findById(userRole.get().getUser().getId());
        if (receiverAdmin.isEmpty()) {
            return ResponseEntity.badRequest().body("Receiver admin not found");
        }

        // Gửi message vào database
        Message message = Message.builder()
                .sender(user.get())
                .receiver(receiverAdmin.get())
                .content(messageRequest.getContent())
                .image(imageUrl)
                .dateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .build();

        message = messageRepository.save(message);

        MessageResponse messageResponse = MessageResponse.builder()
                .messageId(message.getMessageId())
                .senderId(user.get().getId())
                .receiverId(receiverAdmin.get().getId())
                .content(message.getContent())
                .image(message.getImage())
                .dateTime(message.getDateTime())
                .build();

        // Gửi message lên socket
        messageSocketController.broadcastMessage(messageResponse);

        return ResponseEntity.ok().body("Message sent!");
    }

    @Override
    public ResponseEntity<?> replyMessage(AdminMessageRequest messageRequest, MultipartFile image, int adminId) {
        Optional<User> user = userRepository.findById(adminId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Check tin nhắn có rỗng hay không
        if ((messageRequest.getContent() == null || messageRequest.getContent().isBlank()) && image == null) {
            return ResponseEntity.badRequest().body("Content or image must be provided");
        }

        String imageUrl = "";
        if (image != null) {
            try {
                imageUrl = cloudinaryService.uploadImage(image);
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Upload image failed");
            }
        }

        // Nguoi nhan
        Optional<User> receiver = userRepository.findById(messageRequest.getReceiverId());
        if (receiver.isEmpty()) {
            return ResponseEntity.badRequest().body("Receiver admin not found");
        }

        Message message = Message.builder()
                .sender(user.get())
                .receiver(receiver.get())
                .content(messageRequest.getContent())
                .image(imageUrl)
                .dateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .build();

        message = messageRepository.save(message);

        MessageResponse messageResponse = MessageResponse.builder()
                .messageId(message.getMessageId())
                .senderId(user.get().getId())
                .receiverId(receiver.get().getId())
                .content(message.getContent())
                .image(message.getImage())
                .dateTime(message.getDateTime())
                .build();

        // Gửi message lên socket
        messageSocketController.broadcastMessage(messageResponse);

        return ResponseEntity.ok().body("Message sent!");
    }

    @Override
    public ResponseEntity<?> getConversation(int currentUserId, int withUserId) {
        List<Message> messages = messageRepository.findConversation(currentUserId, withUserId);

        List<MessageResponse> messageResponses = messages.stream()
                .map(message -> MessageResponse.builder()
                        .messageId(message.getMessageId())
                        .senderId(message.getSender().getId())
                        .receiverId(message.getReceiver().getId())
                        .content(message.getContent())
                        .image(message.getImage())
                        .dateTime(message.getDateTime())
                        .build())
                .toList();

        return ResponseEntity.ok().body(messageResponses);
    }

    @Override
    public ResponseEntity<?> getUsersChattedWith(int adminId) {
        Optional<User> user = userRepository.findById(adminId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<User> senders = messageRepository.findSendersToAdmin(adminId);
        List<User> receivers = messageRepository.findReceiversFromAdmin(adminId);

        Set<User> users = new HashSet<>();
        users.addAll(senders);
        users.addAll(receivers);

        List<UserResponse> userResponses = users.stream()
                .map(userTmp -> UserResponse.builder()
                        .userId(userTmp.getId())
                        .fullName(userTmp.getFullName())
                        .email(userTmp.getEmail())
                        .active(userTmp.isActive())
                        .build())
                .toList();


        return ResponseEntity.ok().body(userResponses);
    }
}
