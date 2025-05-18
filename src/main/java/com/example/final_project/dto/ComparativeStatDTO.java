package com.example.final_project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
@Builder
public class ComparativeStatDTO {
    private String time;
    private double revenue;
    private double profit;
    private int productsSold;
    private Map<String, Integer> categories;
}
