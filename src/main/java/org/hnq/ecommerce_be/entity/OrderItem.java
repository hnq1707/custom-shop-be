package org.hnq.ecommerce_be.entity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String productId;
    private String size;
    private String type; // PLAIN | UPLOAD | AI
    private String designUrl;
    private Integer quantity;
    private Double price;
}
