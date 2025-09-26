package org.hnq.ecommerce_be.service.impl;

import org.hnq.ecommerce_be.dto.admin.StatsResponse;
import org.hnq.ecommerce_be.dto.order.OrderCreateRequest;
import org.hnq.ecommerce_be.dto.order.OrderItemDto;
import org.hnq.ecommerce_be.dto.order.PaymentInfoDto;
import org.hnq.ecommerce_be.entity.Order;
import org.hnq.ecommerce_be.entity.OrderItem;
import org.hnq.ecommerce_be.entity.PaymentInfo;
import org.hnq.ecommerce_be.repository.OrderRepository;
import org.hnq.ecommerce_be.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(String userId, OrderCreateRequest request) {
        Order order = new Order();
        order.setUserId(userId);
        order.setItems(request.getItems().stream().map(OrderServiceImpl::mapItem).collect(Collectors.toList()));
        order.setStatus("PENDING");
        order.setTotalPrice(request.getTotalPrice());
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentInfo(mapPayment(request.getPaymentInfo()));
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public StatsResponse getStats() {
        List<Order> orders = orderRepository.findAll();
        double total = orders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(o -> o.getTotalPrice() == null ? 0.0 : o.getTotalPrice())
                .sum();

        Map<String, Double> monthly = new HashMap<>();
        orders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .forEach(o -> {
                    LocalDateTime t = o.getCreatedAt();
                    if (t != null) {
                        YearMonth ym = YearMonth.of(t.getYear(), t.getMonth());
                        String key = ym.toString(); // YYYY-MM
                        monthly.merge(key, o.getTotalPrice() == null ? 0.0 : o.getTotalPrice(), Double::sum);
                    }
                });

        return StatsResponse.builder()
                .totalRevenue(total)
                .monthlyRevenue(monthly)
                .build();
    }

    private static OrderItem mapItem(OrderItemDto dto) {
        OrderItem item = new OrderItem();
        item.setProductId(dto.getProductId());
        item.setSize(dto.getSize());
        item.setType(dto.getType());
        item.setDesignUrl(dto.getDesignUrl());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        return item;
    }

    private static PaymentInfo mapPayment(PaymentInfoDto dto) {
        if (dto == null) return null;
        PaymentInfo p = new PaymentInfo();
        p.setMethod(dto.getMethod());
        p.setTxnId(dto.getTxnId());
        return p;
    }
}
