package com.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int address_id;

    private String ward;
    private String wardCode;
    private String district;
    private String districtCode;
    private String province;
    private String provinceCode;

    private String addressDetail;   // Địa chỉ cụ thể

    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
