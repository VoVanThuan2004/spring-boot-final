package com.example.final_project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class BrandResponse {
    private int id;
    private String name;
    private boolean isDeleted;
    private Instant createdAt;
    private Instant updatedAt;

    @JsonProperty("isDeleted")
    public boolean isDeleted() {
        return isDeleted;
    }
}
