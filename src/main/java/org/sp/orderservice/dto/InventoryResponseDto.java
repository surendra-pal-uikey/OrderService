package org.sp.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponseDto {

    private String inventoryId;
    private String productId;
    private String sku;
    private Integer quantityAvailable;
    private Integer reservedQuantity;
    private Integer availableForSale;   // computed: quantityAvailable - reservedQuantity
    private Integer reorderLevel;
    private Integer reorderQuantity;
    private String warehouseLocation;
    private String status;
    private LocalDateTime lastRestockedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
