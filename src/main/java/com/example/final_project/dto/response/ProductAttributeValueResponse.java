package com.example.final_project.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductAttributeValueResponse {

    private int id;

    private int attributeId;

    private String value;

    private int sortOrder;

    private List<ProductAttributeValueImageResponse> images;
}
