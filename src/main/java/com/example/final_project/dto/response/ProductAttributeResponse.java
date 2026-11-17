package com.example.final_project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductAttributeResponse {

    private int id;

    private int productId;

    private String name;

    @JsonProperty("isVisual")
    private boolean isVisual;

    private int sortOrder;

    private List<ProductAttributeValueResponse> values;
}
