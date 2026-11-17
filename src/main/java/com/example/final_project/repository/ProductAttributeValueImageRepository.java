package com.example.final_project.repository;

import com.example.final_project.entity.ProductAttributeValueImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductAttributeValueImageRepository extends JpaRepository<ProductAttributeValueImage, Integer> {

    List<ProductAttributeValueImage> findByAttributeValueIdOrderBySortOrderAsc(int attributeValueId);

    Optional<ProductAttributeValueImage> findByIdAndAttributeValueId(int id, int attributeValueId);

    Optional<ProductAttributeValueImage> findByAttributeValueIdAndIsPrimaryTrue(int attributeValueId);

    @Query("""
            select i
              from ProductAttributeValueImage i, VariantAttributeValue vav
             where vav.attributeValue = i.attributeValue
               and vav.variant.id = :variantId
               and vav.attribute.isVisual = true
             order by i.isPrimary desc, i.sortOrder asc
            """)
    List<ProductAttributeValueImage> findVisualImagesByVariantId(@Param("variantId") int variantId);
}
