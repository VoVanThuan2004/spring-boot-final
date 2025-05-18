package com.example.final_project.entity;

import com.example.final_project.dto.AddressDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fullName;
    private String email;
    private String password;

    private String firebaseUid;

    // Status tài khoản
    private boolean active;

    // Đường dẫn image tài khoản
    private String image;

    // Thời gian tạo tài khoản
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private LoyaltyPoint loyaltyPoint;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Address> addresses;


    private String shippingAddress;
    // Xác thực mã OTP
    private String resetOtp;
    private LocalDateTime resetOtpExpiryDate;
}
