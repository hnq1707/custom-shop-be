package org.hnq.ecommerce_be.service.impl;

import org.hnq.ecommerce_be.dto.admin.StatsResponse;
import org.hnq.ecommerce_be.dto.order.OrderCreateRequest;
import org.hnq.ecommerce_be.dto.order.OrderItemDto;
import org.hnq.ecommerce_be.dto.order.OrderResponse;
import org.hnq.ecommerce_be.dto.order.PaymentInfoDto;
import org.hnq.ecommerce_be.entity.Order;
import org.hnq.ecommerce_be.entity.OrderItem;
import org.hnq.ecommerce_be.entity.PaymentInfo;
import org.hnq.ecommerce_be.entity.User;
import org.hnq.ecommerce_be.repository.OrderRepository;
import org.hnq.ecommerce_be.repository.UserRepository;
import org.hnq.ecommerce_be.service.OrderService;
import org.hnq.ecommerce_be.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
       userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));;
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setItems(request.getItems().stream().map(OrderServiceImpl::mapItem).collect(Collectors.toList()));
        order.setStatus("PENDING");
        order.setTotalPrice(request.getTotalPrice());
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentInfo(mapPayment(request.getPaymentInfo()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUser(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .map(this::toResponse)
                .toList();
    }


    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders =  orderRepository.findAll();
        return orders
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderResponse updateStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
         orderRepository.save(order);
         return toResponse(order);
    }

    @Override
    public StatsResponse getStats() {
        List<Order> orders = orderRepository.findAll();

        // Totals
        double totalRevenue = orders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(o -> o.getTotalPrice() == null ? 0.0 : o.getTotalPrice())
                .sum();
        long totalOrders = orders.size();
        long totalUsers = userRepository.count();

        // Revenue by month for current year
        int year = LocalDate.now().getYear();
        double[] monthly = new double[12];
        for (Order o : orders) {
            if (o.getCreatedAt() == null) continue;
            if (!"COMPLETED".equalsIgnoreCase(o.getStatus())) continue;
            if (o.getCreatedAt().getYear() != year) continue;
            int m = o.getCreatedAt().getMonthValue();
            monthly[m - 1] += (o.getTotalPrice() == null ? 0.0 : o.getTotalPrice());
        }
        List<StatsResponse.RevenuePoint> revenueChart = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            revenueChart.add(StatsResponse.RevenuePoint.builder()
                    .month("T" + (i + 1))
                    .revenue(monthly[i])
                    .build());
        }

        // Monthly growth: compare current vs previous month
        int currMonth = LocalDate.now().getMonthValue();
        double curr = monthly[currMonth - 1];
        double prev = currMonth > 1 ? monthly[currMonth - 2] : 0.0;
        double monthlyGrowth = prev <= 0.0 ? 0.0 : ((curr - prev) / prev) * 100.0;

        // Recent orders (latest 3)
        List<Order> sorted = new ArrayList<>(orders);
        sorted.sort(Comparator.comparing((Order o) -> Optional.ofNullable(o.getCreatedAt()).orElse(LocalDateTime.MIN)).reversed());
        List<StatsResponse.RecentOrder> recentOrders = sorted.stream()
                .limit(3)
                .map(this::toRecentOrder)
                .toList();

        return StatsResponse.builder()
                .stats(StatsResponse.Summary.builder()
                        .totalRevenue(totalRevenue)
                        .totalOrders(totalOrders)
                        .totalUsers(totalUsers)
                        .monthlyGrowth(round1(monthlyGrowth))
                        .build())
                .revenueChart(revenueChart)
                .recentOrders(recentOrders)
                .build();
    }

    @Override
    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));
        return toResponse(order);
    }

    private StatsResponse.RecentOrder toRecentOrder(Order order) {
        String customerName = userRepository.findById(order.getUserId())
                .map(User::getName)
                .orElse("Unknown");
        return StatsResponse.RecentOrder.builder()
                .id(order.getId())
                .customerName(customerName)
                .total(order.getTotalPrice())
                .status(mapStatus(order.getStatus()))
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().toString() : null)
                .build();
    }

    private static String mapStatus(String status) {
        if (status == null) return "pending";
        return switch (status.toUpperCase()) {
            case "SHIPPING" -> "processing";
            case "COMPLETED" -> "delivered";
            default -> "pending";
        };
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    private static OrderItem mapItem(OrderItemDto dto) {
        OrderItem item = new OrderItem();
        item.setProductId(dto.getProductId());
        item.setSize(dto.getSize());
        item.setType(dto.getType());
        if (dto.getDesignUrl() != null && dto.getDesignUrl().startsWith("data:image")) {
            String url = FileUploadUtil.saveBase64Image(dto.getDesignUrl(), "designs");
            item.setDesignUrl(url); // Lưu URL thay vì base64
        } else {
            item.setDesignUrl(dto.getDesignUrl()); // đã là URL thì giữ nguyên
        }
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

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .items(order.getItems())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .shippingAddress(order.getShippingAddress())
                .paymentInfo(order.getPaymentInfo())
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().toString() : null)
                .updatedAt(order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null)
                .build();
    }
}
