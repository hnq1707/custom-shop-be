package org.hnq.ecommerce_be.service;

import org.hnq.ecommerce_be.dto.admin.StatsResponse;
import org.hnq.ecommerce_be.dto.order.OrderCreateRequest;
import org.hnq.ecommerce_be.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(String userId, OrderCreateRequest request);
    List<Order> getOrdersByUser(String userId);

    // Admin
    List<Order> getAllOrders();
    Order updateStatus(String orderId, String status);
    StatsResponse getStats();
}
