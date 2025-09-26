package org.hnq.ecommerce_be.controller;

import jakarta.validation.Valid;
import org.hnq.ecommerce_be.dto.admin.StatsResponse;
import org.hnq.ecommerce_be.dto.admin.UpdateStatusRequest;
import org.hnq.ecommerce_be.entity.Order;
import org.hnq.ecommerce_be.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final OrderService orderService;

    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public List<Order> allOrders() {
        return orderService.getAllOrders();
    }

    @PatchMapping(path = "/orders/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Order updateStatus(@PathVariable("id") String id, @Valid @RequestBody UpdateStatusRequest request) {
        return orderService.updateStatus(id, request.getStatus());
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return orderService.getStats();
    }
}
