package com.example.final_project.service;

import com.example.final_project.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface UserService {
    ResponseEntity<String> createUser(UserCreate userCreate, MultipartFile image);

    ResponseEntity<?> login(UserLogin userLogin);

    ResponseEntity<?> getUserById(int userId);

    ResponseEntity<String> changePassword(int userId, ChangePasswordRequest changePasswordRequest);

    ResponseEntity<?> recoveryPassword(String email);

    ResponseEntity<?> resetPassword(ConfirmOTPRequest confirmOTPRequest);

    Page<UserResponse> getAllUsers(Pageable pageable);

    ResponseEntity<?> updateUser(int userId, UserUpdate userUpdate);

    ResponseEntity<?> deleteUser(int userId);

    ResponseEntity<?> bandUser(int userId);

    ResponseEntity<?> updateUserV2(int userId, UserUpdate userUpdate, MultipartFile image);

//    ResponseEntity<?> firebaseLogin(String idToken);
}
