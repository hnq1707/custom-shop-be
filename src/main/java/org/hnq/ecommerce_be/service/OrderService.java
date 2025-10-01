package org.hnq.ecommerce_be.service;

import org.hnq.ecommerce_be.dto.admin.StatsResponse;
import org.hnq.ecommerce_be.dto.order.OrderCreateRequest;
import org.hnq.ecommerce_be.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderCreateRequest request);
    List<OrderResponse> getOrdersByUser(String userId);

    // Admin
    List<OrderResponse> getAllOrders();
    OrderResponse updateStatus(String orderId, String status);
    StatsResponse getStats();

    OrderResponse getOrderById(String id);
}
