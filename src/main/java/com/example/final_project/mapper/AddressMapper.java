package com.example.final_project.mapper;

import com.example.final_project.dto.response.AddressResponse;
import com.example.final_project.entity.Address;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressMapper {

    public AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .isDefault(address.isDefault())
                .province(address.getProvince())
                .provinceCode(address.getProvinceCode())
                .district(address.getDistrict())
                .districtCode(address.getDistrictCode())
                .ward(address.getWard())
                .wardCode(address.getWardCode())
                .addressDetail(address.getAddressDetail())
                .build();
    }

    public List<AddressResponse> mapToResponseList(List<Address> addressList) {
        return addressList.stream()
                .map(this::mapToResponse)
                .toList();
    }
}
