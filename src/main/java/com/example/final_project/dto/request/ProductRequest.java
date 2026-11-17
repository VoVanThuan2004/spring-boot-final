package com.example.final_project.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class ProductRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal price;

    private String description;

    // Lưu thông số product json (RAM, ROM,...)
    private String specs;

    @Min(1)
    private int categoryId;

    @Min(1)
    private int brandId;
}
