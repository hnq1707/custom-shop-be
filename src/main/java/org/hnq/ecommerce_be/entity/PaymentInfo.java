package org.hnq.ecommerce_be.entity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {
    private String method;   // COD, Paypal...
    private String txnId;
}
