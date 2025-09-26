package org.hnq.ecommerce_be.dto.design;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DesignGenerateRequest {
    @NotBlank(message = "description is required")
    private String description; // Vietnamese text
}
