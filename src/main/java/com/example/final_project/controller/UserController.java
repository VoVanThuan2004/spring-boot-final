package com.example.final_project.controller;

import com.example.final_project.dto.*;
import com.example.final_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Rest: RestfulAPI
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5000")
public class UserController {
    private final UserService userService;

    @PostMapping("/api/v1/register/user/")
    public ResponseEntity<String> createUser(
            @RequestPart UserCreate userCreate,
            @RequestPart(required = false) MultipartFile image
    ) {
        return userService.createUser(userCreate, image);
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<?> login(@RequestBody UserLogin userLogin) {
        return userService.login(userLogin);
    }


    // Xem chi tiết người dùng
    @GetMapping("/api/v1/profile/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    // Lấy list user + pagination
    @GetMapping("/api/v1/users")
    public Page<UserResponse> getAllUsers(
            @RequestParam int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getAllUsers(pageable);
    }

    // Update thông tin user
    @PutMapping("/api/v1/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody UserUpdate userUpdate) {
        return userService.updateUser(userId, userUpdate);
    }

    // Update thông tin user v2
    @PutMapping("/api/v2/users/{userId}")
    public ResponseEntity<?> updateUserV2(
            @PathVariable int userId,
            @RequestPart UserUpdate userUpdate,
            @RequestPart(required = false) MultipartFile image
    ) {
        return userService.updateUserV2(userId, userUpdate, image);
    }

    // Delete user
    // Xóa bảng con liên kết trước, sau đó xóa bảng cha
    @DeleteMapping("/api/v1/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        return userService.deleteUser(userId);
    }

    // Banning user
    @PutMapping("/api/v1/users/banning/{userId}")
    public ResponseEntity<?> bandUser(@PathVariable int userId) {
        return userService.bandUser(userId);
    }

    // thay doi password
    @PostMapping("/api/v1/change-password/user")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getId();
        return userService.changePassword(userId,  changePasswordRequest);
    }

    // Recovery password
    // Send mã OTP qua email
    @PostMapping("/api/v1/recovery/password")
    public ResponseEntity<?> recoveryPassword(@RequestParam String email) {
        return userService.recoveryPassword(email);
    }

    /**
     * Sau khi có mã otp, nhập email, mật khẩu mới, otp để xác thực
     */

    @PostMapping("/api/v1/reset/password")
    public ResponseEntity<?> resetPassword(@RequestBody ConfirmOTPRequest confirmOTPRequest) {
        return userService.resetPassword(confirmOTPRequest);
    }

}
