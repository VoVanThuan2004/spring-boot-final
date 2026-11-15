package com.example.final_project.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
public class RevenueTrendDTOSimple {
    private double revenue;
    private int month;
}
