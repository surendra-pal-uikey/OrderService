package org.sp.orderservice.services;

import org.sp.orderservice.dto.OrderRequestDto;
import org.sp.orderservice.dto.OrderResponseDto;
import org.sp.orderservice.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDto placeOrder(OrderRequestDto requestDto);

    OrderResponseDto getOrderById(String orderId);

    List<OrderResponseDto> getOrdersByCustomerId(String customerId);

    List<OrderResponseDto> getAllOrders();

    OrderResponseDto updateOrderStatus(String orderId, OrderStatus newStatus);

    void cancelOrder(String orderId);

}
