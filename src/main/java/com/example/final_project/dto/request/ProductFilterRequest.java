package com.example.final_project.dto.request;

import com.example.final_project.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductFilterRequest {

    @Schema(description = "Tìm gần đúng theo tên sản phẩm", example = "Dell")
    private String keyword = "";

    @Schema(description = "Danh sách ID danh mục", example = "1,2")
    private List<Integer> categoryIds;

    @Schema(description = "Danh sách ID thương hiệu", example = "1,2")
    private List<Integer> brandIds;

    @Schema(description = "Giá tối thiểu", example = "0")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal minPrice = BigDecimal.ZERO;

    @Schema(description = "Giá tối đa; bỏ trống để không giới hạn", example = "30000000")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal maxPrice;

    @Schema(description = "Trạng thái sản phẩm", example = "ACTIVE")
    private ProductStatus status = ProductStatus.ACTIVE;

    @Schema(description = "Trang bắt đầu từ 0", example = "0")
    @Min(0)
    private int page = 0;

    @Schema(description = "Số sản phẩm mỗi trang, tối đa 100", example = "10")
    @Min(1)
    @Max(100)
    private int size = 10;

    @Schema(description = "Field sắp xếp", example = "price")
    private String sortBy = "name";

    @Schema(description = "Chiều sắp xếp: asc hoặc desc", example = "asc")
    private String direction = "asc";
}
