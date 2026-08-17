package org.sp.orderservice.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "product-service", url = "${product-service.url}")
public interface ProductClient {
}
