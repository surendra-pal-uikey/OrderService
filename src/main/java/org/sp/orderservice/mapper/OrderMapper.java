package org.sp.orderservice.mapper;

import org.sp.orderservice.dto.OrderItemResponseDto;
import org.sp.orderservice.dto.OrderResponseDto;
import org.sp.orderservice.models.Order;
import org.sp.orderservice.models.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponseDto toResponseDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems()
                .stream()
                .map(OrderMapper::toItemResponseDto)
                .collect(Collectors.toList());

        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .orderItems(itemDtos)
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus() != null ? order.getOrderStatus().name() : null)
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public static OrderItemResponseDto toItemResponseDto(OrderItem item) {
        return OrderItemResponseDto.builder()
                .productId(item.getProductId())
                .sku(item.getSku())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .pricePerUnit(item.getPricePerUnit())
                .subtotal(item.getSubtotal())
                .build();
    }
}
