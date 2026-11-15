package com.example.final_project.service;

import com.example.final_project.dto.request.AddressRequest;
import com.example.final_project.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddresses(int userId);

    AddressResponse addAddress(int userId, AddressRequest request);

    AddressResponse updateAddress(int userId, int addressId, AddressRequest request);

    AddressResponse setDefaultAddress(int userId, int addressId);

    void deleteAddress(int userId, int addressId);
}
