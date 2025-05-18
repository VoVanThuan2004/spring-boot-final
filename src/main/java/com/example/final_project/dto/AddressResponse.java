package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class AddressResponse {
    private int addressId;

    private boolean isDefault;  // Địa chỉ mặc định: true
    private String ward;
    private String wardCode;
    private String district;
    private String districtCode;
    private String province;
    private String provinceCode;
    private String addressDetail;
}
