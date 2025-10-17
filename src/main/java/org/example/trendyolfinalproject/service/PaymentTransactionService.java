package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.dao.entity.PaymentMethod;
import org.example.trendyolfinalproject.dao.entity.PaymentTransaction;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.enums.Currency;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.PaymentTransactionResponse;
import org.example.trendyolfinalproject.model.response.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentTransactionService {

    void savePaymentTransaction(PaymentTransaction transaction);

    void createSuccessPaymentTransactionFromAdminToSellers(User sender, User receiver, BigDecimal amount, PaymentMethod paymentMethod);

    PaymentTransaction createSuccessPaymentTransaction(Order order);

    PaymentTransaction createFailedPaymentTransaction(PaymentMethod paymentMethod, BigDecimal amount, Currency currency);

    void returnedPaymentTransaction(Order order, Long orderId);

    ApiResponse<List<TransactionResponse>> getPaymentTransaction(Long paymentMethodId);

    ApiResponse<List<TransactionResponse>> getAllTransactions();

}
