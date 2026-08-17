package org.sp.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private String productId;
    private String sku;
    private String productName;
    private String category;
    private String brand;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String description;
    private String imageUrl;
    private Integer stockQuantity;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}