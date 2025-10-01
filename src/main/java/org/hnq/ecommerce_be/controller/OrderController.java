package org.hnq.ecommerce_be.controller;

import jakarta.validation.Valid;
import org.hnq.ecommerce_be.dto.auth.UserDto;
import org.hnq.ecommerce_be.dto.order.OrderCreateRequest;
import org.hnq.ecommerce_be.dto.order.OrderResponse;
import org.hnq.ecommerce_be.entity.Order;
import org.hnq.ecommerce_be.service.OrderService;
import org.hnq.ecommerce_be.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderResponse create(@Valid @RequestBody OrderCreateRequest request) {
        return orderService.createOrder(request);
        
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable("id") String id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/me")
    public List<OrderResponse> myOrders(@RequestParam("userId") String userId) {
        UserDto me = userService.getCurrentUser(userId);
        return orderService.getOrdersByUser(me.getId());
    }
}
