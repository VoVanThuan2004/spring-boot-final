package com.example.final_project.service;

import com.example.final_project.dto.AddressDTO;
import org.springframework.http.ResponseEntity;

public interface AddressService {
    ResponseEntity<?> addAddress(int userId, AddressDTO addressDTO);

    ResponseEntity<?> updateDefaultAddress(int userId, int addressId);

    ResponseEntity<?> deleteAddress(int addressId);
}
