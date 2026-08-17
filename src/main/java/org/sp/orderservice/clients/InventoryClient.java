package org.sp.orderservice.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "inventory-service", url = "${inventory-service.url}")
public interface InventoryClient {

}
