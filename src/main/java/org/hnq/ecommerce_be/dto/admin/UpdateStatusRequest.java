package org.hnq.ecommerce_be.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotBlank
    private String status; // PENDING | SHIPPING | COMPLETED
}
