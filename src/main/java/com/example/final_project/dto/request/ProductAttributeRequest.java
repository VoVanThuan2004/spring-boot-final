package com.example.final_project.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductAttributeRequest {

    @NotBlank(message = "Attribute name is required")
    @Size(max = 50, message = "Attribute name must not exceed 50 characters")
    private String name;

    @JsonProperty("isVisual")
    private Boolean isVisual;

    @Min(value = 0, message = "sortOrder must be greater than or equal to 0")
    private Integer sortOrder;
}
