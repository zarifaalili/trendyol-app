package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.model.request.AddBalanceRequest;
import org.example.trendyolfinalproject.model.request.ChangeDefaultPaymentMethod;
import org.example.trendyolfinalproject.model.request.PaymentMethodCreateRequest;
import org.example.trendyolfinalproject.model.request.PaymentRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.PaymentMethodResponse;
import org.example.trendyolfinalproject.model.response.PaymentResponse;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentMethodService {

    ApiResponse<PaymentMethodResponse> createPaymentMethod(PaymentMethodCreateRequest request);

    ApiResponse<String> addbalance(AddBalanceRequest request);

    ApiResponse<PaymentMethodResponse> changeDefaultPaymentMethod(ChangeDefaultPaymentMethod request);

    ApiResponse<String> payToSellers(Map<Seller, BigDecimal> earnings);

    ApiResponse<PaymentResponse> pay(PaymentRequest request);


}
