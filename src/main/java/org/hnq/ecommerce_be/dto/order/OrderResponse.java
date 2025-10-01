package org.hnq.ecommerce_be.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hnq.ecommerce_be.entity.OrderItem;
import org.hnq.ecommerce_be.entity.PaymentInfo;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String userId;
    private List<OrderItem> items;
    private String status;
    private Double totalPrice;
    private String shippingAddress;
    private PaymentInfo paymentInfo;
    private String createdAt; // dùng String thay cho LocalDateTime
    private String updatedAt;
}
