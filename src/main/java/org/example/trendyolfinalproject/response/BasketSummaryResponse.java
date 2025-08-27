package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.trendyolfinalproject.dao.entity.BasketElement;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class BasketSummaryResponse {
    private List<BasketElementResponse> basketElements;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

}
