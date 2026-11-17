package com.example.final_project.controller;

import com.example.final_project.dto.CustomUserDetails;
import com.example.final_project.dto.response.ApiResponse;
import com.example.final_project.dto.response.WishListResponse;
import com.example.final_project.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wish-lists")
public class WishListController {
    private final WishListService wishListService;

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> addToWishList(
            @PathVariable Integer productId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        wishListService.addToWishList(productId, customUserDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Added product in wish list", null)
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> removeFromWishList(
            @PathVariable Integer productId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        wishListService.removeFromWishList(productId, customUserDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Remove product from wish list", null)
        );
    }

    // Xóa toàn bộ wish list của user
    @DeleteMapping("")
    public ResponseEntity<ApiResponse<?>> clearMyWishList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        wishListService.clearMyWishList(customUserDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Clear all wish lists", null)
        );
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<WishListResponse>>> getWishLists(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<WishListResponse> wishListResponses = wishListService.getWishLists(customUserDetails.getUserId(), page, size);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Get wish lists", wishListResponses)
        );
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> checkProductInWishList(
            @PathVariable Integer productId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
       Boolean isProductInWishList = wishListService.checkProductInWishList(productId, customUserDetails.getUserId());

       return ResponseEntity.status(HttpStatus.OK).body(
               ApiResponse.success("Check product in wish list", isProductInWishList)
       );
    }
}
