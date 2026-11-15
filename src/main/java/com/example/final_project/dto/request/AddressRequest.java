package com.example.final_project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @NotBlank(message = "province is required")
    private String province;
    private String provinceCode;

    @NotBlank(message = "district is required")
    private String district;
    private String districtCode;

    @NotBlank(message = "ward is required")
    private String ward;
    private String wardCode;

    @NotBlank(message = "addressDetail is required")
    private String addressDetail;
}
