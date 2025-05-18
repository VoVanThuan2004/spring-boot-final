package com.example.final_project.dto;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private String ward;
    private String wardCode;
    private String district;
    private String districtCode;
    private String province;
    private String provinceCode;
    private String addressDetail;
}
