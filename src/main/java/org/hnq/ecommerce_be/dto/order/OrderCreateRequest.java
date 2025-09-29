package org.hnq.ecommerce_be.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {

    String userId;

    @NotEmpty
    private List<@Valid OrderItemDto> items;

    @NotNull
    private Double totalPrice;

    private String shippingAddress;

    @Valid
    private PaymentInfoDto paymentInfo;
}
