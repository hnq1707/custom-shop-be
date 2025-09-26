package org.hnq.ecommerce_be.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private Double totalRevenue;
    // key format YYYY-MM, value revenue
    private Map<String, Double> monthlyRevenue;
}
