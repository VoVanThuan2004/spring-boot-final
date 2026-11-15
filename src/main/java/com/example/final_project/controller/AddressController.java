package com.example.final_project.controller;

import com.example.final_project.dto.CustomUserDetails;
import com.example.final_project.dto.request.AddressRequest;
import com.example.final_project.dto.response.AddressResponse;
import com.example.final_project.dto.response.ApiResponse;
import com.example.final_project.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(addressService.getAddresses(userDetails.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse data = addressService.addAddress(userDetails.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AddressResponse>builder()
                        .success(true)
                        .code(HttpStatus.CREATED.value())
                        .message("Address created")
                        .data(data)
                        .build());
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable int addressId,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Address updated",
                addressService.updateAddress(userDetails.getId(), addressId, request)));
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable int addressId) {
        return ResponseEntity.ok(ApiResponse.success("Default address updated",
                addressService.setDefaultAddress(userDetails.getId(), addressId)));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable int addressId) {
        addressService.deleteAddress(userDetails.getId(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted", null));
    }
}
