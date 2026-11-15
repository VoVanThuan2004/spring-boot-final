package com.example.final_project.service;

import com.example.final_project.cloudinary.CloudinaryService;
import com.example.final_project.dto.request.*;
import com.example.final_project.dto.response.*;
import com.example.final_project.dto.TokenPayload;
import com.example.final_project.entity.*;
import com.example.final_project.mapper.AddressMapper;
import com.example.final_project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final AddressRepository addressRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;
    private final CloudinaryService cloudinaryService;
    private final AddressMapper addressMapper;

    @Override
    public ResponseEntity<String> createUser(UserCreate userCreate, MultipartFile image) {
        // Nếu chưa có role: user -> tạo role
        UserRole userRole = new UserRole();
        Optional<Role> role_user = roleRepository.findByName("USER");
        if (!role_user.isPresent()) {
            Role role = new Role();
            role.setName("USER");
            roleRepository.save(role);

            userRole.setRole(role);
        }
        else {
            userRole.setRole(role_user.get());
        }

        // check email đã tồn tại
        Optional<User> checkUser = userRepository.findByEmail(userCreate.getEmail());
        if (checkUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email has existed!");
        }


        User user = User.builder()
                .fullName(userCreate.getFullName())
                .email(userCreate.getEmail())
                .password(passwordEncoder.encode(userCreate.getPassword()))
                .active(true)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .build();

        // Upload image
        String urlImage = "";
        if (image != null) {
            try {
                urlImage = cloudinaryService.uploadImage(image);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed!");
            }
        }

        user.setImage(urlImage);
        user = userRepository.save(user);

        userRole.setUser(user);
        userRoleRepository.save(userRole);

        // Lưu address
        Address address = Address.builder()
                .ward(userCreate.getAddress().getWard())
                .wardCode(userCreate.getAddress().getWardCode())
                .district(userCreate.getAddress().getDistrict())
                .districtCode(userCreate.getAddress().getDistrictCode())
                .province(userCreate.getAddress().getProvince())
                .provinceCode(userCreate.getAddress().getProvinceCode())
                .addressDetail(userCreate.getAddress().getAddressDetail())
                .isDefault(true)
                .user(user)
                .build();

        addressRepository.save(address);
        return ResponseEntity.ok().body("Account user created");

    }

    @Override
    public ResponseEntity<?> getUserById(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Address> addresses = addressRepository.findByUserId(userId);

            int loyaltyPoints = user.get().getLoyaltyPoint() != null ? user.get().getLoyaltyPoint().getPoints() : 0;
            UserResponse userResponse = UserResponse.builder()
                    .userId(user.get().getId())
                    .image(user.get().getImage())
                    .fullName(user.get().getFullName())
                    .email(user.get().getEmail())
                    .active(user.get().isActive())
                    .loyaltyPoints(loyaltyPoints)
                    .addresses(addressMapper.mapToResponseList(addresses))
                    .build();

            return ResponseEntity.ok().body(userResponse);
        }
        return ResponseEntity.badRequest().body("User must be login");
    }

    @Override
    @Transactional
    public ResponseEntity<String> changePassword(int userId, ChangePasswordRequest changePasswordRequest) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.get().getPassword())) {
            return ResponseEntity.badRequest().body("Old password is incorrect");
        }

        user.get().setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user.get());
        refreshTokenService.revokeAllForUser(userId);

        return ResponseEntity.ok().body("Password changed");
    }

    @Override
    public ResponseEntity<?> recoveryPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Sinh mã OTP (6 số)
        String otp = String.format("%06d", new Random().nextInt(1000000));

        // Lưu thông tin OTP vào user
        user.get().setResetOtp(otp);
        user.get().setResetOtpExpiryDate(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(5));
        userRepository.save(user.get());

        // Gửi email thông tin báo
        emailService.sendOTPEmailRecoveryPassword(user.get().getEmail(), otp);

        return ResponseEntity.ok("Mã OTP đã được gửi đến email của bạn.");
    }

    @Override
    @Transactional
    public ResponseEntity<?> resetPassword(ConfirmOTPRequest confirmOTPRequest) {
        // Check email
        Optional<User> user = userRepository.findByEmail(confirmOTPRequest.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not found");
        }

        // Check OTP
        if (!confirmOTPRequest.getOtp().equals(user.get().getResetOtp())) {
            return ResponseEntity.badRequest().body("OTP is incorrect");
        }

        // Check thời hạn mã otp
        if (user.get().getResetOtpExpiryDate().isBefore(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))) {
            return ResponseEntity.badRequest().body("OTP expired");
        }


        // Set password moi
        user.get().setPassword(passwordEncoder.encode(confirmOTPRequest.getPassword()));

        // Xoa ma otp
        user.get().setResetOtp(null);
        user.get().setResetOtpExpiryDate(null);
        userRepository.save(user.get());
        refreshTokenService.revokeAllForUser(user.get().getId());

        return ResponseEntity.ok().body("Reset password successfully");
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        // Optional<Role> role = roleRepository.findByName("ADMIN");
        // if (role.isEmpty()) {
        //     return null;
        // }
        // Optional<UserRole> userRole = userRoleRepository.findByRoleId(role.get().getId());
        // if (userRole.isEmpty()) {
        //     return null;
        // }
        Page<User> users = userRepository.findByIdNot(1, pageable);

        return users.map(
                user -> {
                    UserResponse userResponse = UserResponse.builder()
                            .userId(user.getId())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .active(user.isActive())
                            .image(user.getImage())
                            .build();

                    userResponse.setAddresses(addressMapper.mapToResponseList(user.getAddresses()));
                    return userResponse;
        });
    }

    @Override
    public ResponseEntity<?> updateUser(int userId, UserUpdate userUpdate) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        user.get().setFullName(userUpdate.getFullName());
        userRepository.save(user.get());

        return ResponseEntity.ok().body("User updated successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        userRepository.delete(user.get());
        return ResponseEntity.ok().body("User deleted successfully");
    }

    @Override
    public ResponseEntity<?> bandUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        boolean currentStatus = user.get().isActive();
        user.get().setActive(!currentStatus);
        userRepository.save(user.get());

        return ResponseEntity.ok().body("User updated");
    }

    @Override
    public ResponseEntity<?> updateUserV2(int userId, UserUpdate userUpdate, MultipartFile image) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }


        // check image
        String urlImage = "";
        if (image != null) {
            try {
                urlImage = cloudinaryService.uploadImage(image);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error while uploading image");
            }
        }

        // update
        user.get().setFullName(userUpdate.getFullName());
        user.get().setImage(Objects.equals(urlImage, "") ? user.get().getImage() : urlImage);
        userRepository.save(user.get());

        return ResponseEntity.ok().body("User updated successfully");
    }

//    @Override
//    public ResponseEntity<?> firebaseLogin(String idToken) {
//        try {
//            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
//            String email = decodedToken.getEmail();
//            String fullName = decodedToken.getName();
//            String firebaseUid = decodedToken.getUid();
//
//            Optional<User> userOptional = userRepository.findByEmail(email);
//            User user;
//
//            if (userOptional.isEmpty()) {
//                // Tao user
//                user = User.builder()
//                        .email(email)
//                        .fullName(fullName)
//                        .firebaseUid(firebaseUid)
//                        .password("")
//                        .build();
//
//                user = userRepository.save(user);
//
//                // Gan role
//                Role role = roleRepository.findByName("USER")
//                        .orElseGet(() -> roleRepository.save(new Role("USER")));
//                UserRole userRole = new UserRole();
//                userRole.setRole(role);
//                userRole.setUser(user);
//                userRoleRepository.save(userRole);
//            }
//            else {
//                user = userOptional.get();
//            }
//
//            // Lấy role
//            Optional<UserRole> userRole = userRoleRepository.findByUserId(user.getId());
//            String roleName = userRole.map(r -> r.getRole().getName()).orElse("USER");
//
//            // Sinh token từ backend
//            TokenPayload payload = TokenPayload.builder()
//                    .userId(user.getId())
//                    .fullName(user.getFullName())
//                    .role(roleName)
//                    .build();
//
//            String accessToken = jwtTokenUtil.generateToken(payload, 15 * 60); // 15 phút
//
//            return ResponseEntity.ok(LoginResponse.builder()
//                    .accessToken(accessToken)
//                    .user(UserResponse.builder()
//                            .fullName(user.getFullName())
//                            .email(user.getEmail())
//                            .build())
//                    .build());
//        } catch (FirebaseAuthException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Firebase Token");
//        }
//    }
}
