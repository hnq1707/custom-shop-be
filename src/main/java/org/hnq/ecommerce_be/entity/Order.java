package org.hnq.ecommerce_be.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("orders")
public class Order {
    @Id
    private String id;

    private String userId;

    private List<OrderItem> items;

    // PENDING | SHIPPING | COMPLETED
    private String status;

    private Double totalPrice;

    private String shippingAddress;

    private PaymentInfo paymentInfo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
