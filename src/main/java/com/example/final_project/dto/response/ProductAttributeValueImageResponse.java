package com.example.final_project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductAttributeValueImageResponse {

    private int id;

    private String imageUrl;

    private int sortOrder;

    @JsonProperty("isPrimary")
    private boolean isPrimary;
}
