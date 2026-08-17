package org.sp.orderservice.clients;

import org.sp.orderservice.dto.InventoryResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "${inventory-service.url}")
public interface InventoryClient {

    @PostMapping("/api/inventory/product/{productId}/reserve")
    InventoryResponseDto reserveStockByProductId(@PathVariable("productId") String productId,
                                                 @RequestParam("quantity") Integer quantity);

    @PostMapping("/api/inventory/product/{productId}/deduct")
    InventoryResponseDto deductStockByProductId(@PathVariable("productId") String productId,
                                                @RequestParam("quantity") Integer quantity);

    @PostMapping("/api/inventory/product/{productId}/release")
    InventoryResponseDto releaseStockByProductId(@PathVariable("productId") String productId,
                                                 @RequestParam("quantity") Integer quantity);
}
