package com.example.final_project.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductAttributeValueRequest {

    @NotBlank(message = "Attribute value is required")
    @Size(max = 100, message = "Attribute value must not exceed 100 characters")
    private String value;

    @Min(value = 0, message = "sortOrder must be greater than or equal to 0")
    private Integer sortOrder;
}
