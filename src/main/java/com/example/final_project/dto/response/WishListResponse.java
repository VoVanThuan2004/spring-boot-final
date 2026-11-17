package com.example.final_project.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class WishListResponse {
    private long wishListId;
    private int productId;
    private String productName;
    private BigDecimal productPrice;
    private String productImage;
    private Instant addedAt;
}
