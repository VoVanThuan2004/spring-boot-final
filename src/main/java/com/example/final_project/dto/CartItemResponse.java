package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class CartItemResponse {
    private int cartItemId;
    private int productVariantId;  // ID của variant sản phẩm trong giỏ hàng
    private String productName;  // Tên sản phẩm
    private double price;  // Giá của sản phẩm
    private int quantity;  // Số lượng sản phẩm trong giỏ hàng
    private String image;  // Hình ảnh của sản phẩm (nếu có)
}
