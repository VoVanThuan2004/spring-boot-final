package com.example.final_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private int imageId;

    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "productvariant_id")
    private ProductVariant productVariant;
}
