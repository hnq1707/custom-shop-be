package org.hnq.ecommerce_be.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemDto {
    @NotBlank
    private String productId;

    @NotBlank
    private String size;

    @NotBlank
    private String type; // PLAIN | UPLOAD | AI

    private String designUrl;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private Double price;
}
