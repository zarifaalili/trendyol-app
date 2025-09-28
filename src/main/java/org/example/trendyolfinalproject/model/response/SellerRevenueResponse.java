package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SellerRevenueResponse {
    private BigDecimal totalRevenue;
    private Long totalOrders;
}
