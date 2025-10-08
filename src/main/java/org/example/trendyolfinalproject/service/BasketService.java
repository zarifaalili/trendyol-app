package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.BasketSummaryResponse;

import java.math.BigDecimal;

public interface BasketService {

    ApiResponse<BigDecimal> getTotalAmount();

    ApiResponse<BigDecimal> calculateRawTotalAmount();

    int notifyAbandonedBaskets();

    ApiResponse<BasketSummaryResponse> getBasketSummary();

}
