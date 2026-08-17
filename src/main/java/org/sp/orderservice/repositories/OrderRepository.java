package org.sp.orderservice.repositories;

import org.sp.orderservice.enums.OrderStatus;
import org.sp.orderservice.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByCustomerId(String customerId);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    List<Order> findByCustomerIdAndOrderStatus(String customerId, OrderStatus orderStatus);
}