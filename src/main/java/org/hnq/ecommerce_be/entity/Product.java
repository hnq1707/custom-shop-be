package org.hnq.ecommerce_be.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("products")
public class Product {
    @Id
    private String id;

    // PLAIN | UPLOAD | AI
    private String type;

    // S, M, L, XL
    private String size;

    // null if plain
    private String designUrl;

    private Double price;

    private LocalDateTime createdAt;
}
