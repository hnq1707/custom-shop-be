package org.hnq.ecommerce_be.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private Summary stats;
    private List<RevenuePoint> revenueChart;
    private List<RecentOrder> recentOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Double totalRevenue;
        private Long totalOrders;
        private Long totalUsers;
        private Double monthlyGrowth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenuePoint {
        private String month; // e.g., T1..T12
        private Double revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentOrder {
        private String id;
        private String customerName;
        private Double total;
        private String status; // pending | processing | delivered
        private String createdAt; // ISO string
    }
}
