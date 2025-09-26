package org.hnq.ecommerce_be.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank
    private String type; // PLAIN | UPLOAD | AI

    @NotBlank
    private String size; // S, M, L, XL

    private String designUrl; // optional

    @NotNull
    private Double price;
}
