package com.example.final_project.controller;

import com.example.final_project.dto.AddressDTO;
import com.example.final_project.dto.CustomUserDetails;
import com.example.final_project.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    // Thêm địa chỉ
    @PostMapping("/api/v1/address")
    public ResponseEntity<?> addAddress(@RequestBody AddressDTO addressDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getId();
        return addressService.addAddress(userId, addressDTO);
    }

    // Thiết lập địa chỉ mặc định
    @PutMapping("/api/v1/address/default/{addressId}")
    public ResponseEntity<?> updateDefaultAddress(@PathVariable int addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getId();
        return addressService.updateDefaultAddress(userId, addressId);
    }

    // Xóa địa chỉ
    @DeleteMapping("/api/v1/address/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable int addressId) {
        return addressService.deleteAddress(addressId);
    }
}
