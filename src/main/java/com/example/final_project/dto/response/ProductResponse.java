package com.example.final_project.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class ProductResponse {
    private int id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private String description;
    private String slug;

    // Lưu thông số product (RAM, ROM,...)
    private String specs;
    private Instant createdAt;
}
