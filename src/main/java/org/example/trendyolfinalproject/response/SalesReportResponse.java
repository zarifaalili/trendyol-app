package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SalesReportResponse {
    private BigDecimal totalRevenue;
    private Long totalCouponsUsed;
    private String mostSoldProduct;
    private Long activeUsers;
}

