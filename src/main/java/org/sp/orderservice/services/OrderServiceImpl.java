package org.sp.orderservice.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sp.orderservice.clients.InventoryClient;
import org.sp.orderservice.clients.ProductClient;
import org.sp.orderservice.dto.*;
import org.sp.orderservice.enums.OrderStatus;
import org.sp.orderservice.exceptions.InsufficientStockException;
import org.sp.orderservice.exceptions.InventoryServiceException;
import org.sp.orderservice.exceptions.OrderNotFoundException;
import org.sp.orderservice.exceptions.ProductNotFoundException;
import org.sp.orderservice.mapper.OrderMapper;
import org.sp.orderservice.models.Order;
import org.sp.orderservice.models.OrderItem;
import org.sp.orderservice.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {

        Order order = Order.builder()
                .customerId(requestDto.getCustomerId())
                .shippingAddress(requestDto.getShippingAddress())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : requestDto.getOrderItems()) {

            ProductResponseDto product = fetchProductOrThrow(itemDto.getProductId());

            // reserve stock — if insufficient stock, InventoryService throws and this propagates,
            // rolling back the whole order due to @Transactional
            try {
                InventoryResponseDto inventoryResponseDto = reserveInventoryOrThrow(itemDto.getProductId(), itemDto.getQuantity());
                logger.info("inventory resp{}", inventoryResponseDto);
            } catch (FeignException.BadRequest ex) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + itemDto.getProductId());
            } catch (FeignException ex) {
                throw new InventoryServiceException(
                        "Inventory service unavailable while reserving stock for product: " + itemDto.getProductId(), ex);
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemDto.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getProductId())
                    .sku(product.getSku())
                    .productName(product.getProductName())
                    .quantity(itemDto.getQuantity())
                    .pricePerUnit(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            order.addOrderItem(orderItem);
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return OrderMapper.toResponseDto(saved);
    }

    @Override
    public OrderResponseDto getOrderById(String orderId) {
        Order order = findOrderOrThrow(orderId);
        return OrderMapper.toResponseDto(order);
    }

    @Override
    public List<OrderResponseDto> getOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = findOrderOrThrow(orderId);

        if (newStatus == OrderStatus.SHIPPED && order.getOrderStatus() != OrderStatus.SHIPPED) {
            for (OrderItem item : order.getOrderItems()) {
                inventoryClient.deductStockByProductId(item.getProductId(), item.getQuantity());
            }
        }

        order.setOrderStatus(newStatus);
        Order updated = orderRepository.save(order);
        return OrderMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    public void cancelOrder(String orderId) {
        Order order = findOrderOrThrow(orderId);

        for (OrderItem item : order.getOrderItems()) {
            inventoryClient.releaseStockByProductId(item.getProductId(), item.getQuantity());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // ---------- helpers ----------

    private Order findOrderOrThrow(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + orderId));
    }

    private ProductResponseDto fetchProductOrThrow(String productId) {
        try {
            return productClient.getProductById(productId);
        } catch (FeignException.NotFound ex) {
            throw new ProductNotFoundException(
                    "Cannot place order — product not found: " + productId);
        }
    }

    private InventoryResponseDto reserveInventoryOrThrow(String productId, Integer quantity) {
        try {
            return inventoryClient.reserveStockByProductId(productId, quantity);
        } catch (FeignException.NotFound ex) {
            throw new InventoryServiceException(
                    "Cannot place order — inventory not found: " + productId);
        }
    }
}