package org.hnq.ecommerce_be.dto.order;

import lombok.Data;

@Data
public class PaymentInfoDto {
    private String method;   // COD, Paypal...
    private String txnId;
}
