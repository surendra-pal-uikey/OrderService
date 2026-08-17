package org.sp.orderservice.clients;

import org.sp.orderservice.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${product-service.url}")
public interface ProductClient {

    @GetMapping("/api/products/{productId}")
    ProductResponseDto getProductById(@PathVariable("productId") String productId);
}
