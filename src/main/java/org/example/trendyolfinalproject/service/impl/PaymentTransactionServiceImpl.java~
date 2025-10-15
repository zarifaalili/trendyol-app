package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.client.TransactionClient;
import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.dao.entity.PaymentMethod;
import org.example.trendyolfinalproject.dao.entity.PaymentTransaction;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.PaymentMethodRepository;
import org.example.trendyolfinalproject.dao.repository.PaymentTransactionRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.PaymentTransactionMapper;
import org.example.trendyolfinalproject.model.enums.Currency;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.PaymentTransactionResponse;
import org.example.trendyolfinalproject.model.response.TransactionResponse;
import org.example.trendyolfinalproject.service.PaymentTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final TransactionClient transactionClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void savePaymentTransaction(PaymentTransaction transaction) {
        paymentTransactionRepository.save(transaction);
    }

    @Override
    public void createSuccessPaymentTransactionFromAdminToSellers(User sender, User receiver, BigDecimal amount, PaymentMethod paymentMethod) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setCurrency(Currency.AZN);
        transaction.setStatus(Status.SUCCESS);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setPayment(paymentMethod);
        transaction.setTransactionId(generateTransactionId());
        transaction.setProviderResponse("successful payment");
        paymentTransactionRepository.save(transaction);
    }

    @Override
    public PaymentTransaction createSuccessPaymentTransaction(Order order) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setPayment(order.getPaymentMethodId());
        paymentTransaction.setAmount(order.getTotalAmount());
        paymentTransaction.setCurrency(order.getPaymentMethodId().getCurrency());
        paymentTransaction.setTransactionDate(LocalDateTime.now());
        paymentTransaction.setTransactionId(generateTransactionId());
        paymentTransaction.setOrder(order);
        paymentTransaction.setStatus(Status.PENDING);
        paymentTransaction.setProviderResponse("successful payment");
        return paymentTransactionRepository.save(paymentTransaction);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public PaymentTransaction createFailedPaymentTransaction(PaymentMethod paymentMethod, BigDecimal amount, Currency currency) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setPayment(paymentMethod);
        paymentTransaction.setAmount(amount);
        paymentTransaction.setCurrency(currency);
        paymentTransaction.setTransactionDate(LocalDateTime.now());
        paymentTransaction.setTransactionId(generateTransactionId());
        paymentTransaction.setOrder(null);
        paymentTransaction.setStatus(Status.FAILED);
        paymentTransaction.setProviderResponse("balance is not enough");
        return paymentTransactionRepository.save(paymentTransaction);
    }

    @Override
    public void returnedPaymentTransaction(Order order, Long orderId) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setOrder(order);
        paymentTransaction.setPayment(order.getPaymentMethodId());
        paymentTransaction.setTransactionId(generateTransactionId());
        paymentTransaction.setAmount(order.getTotalAmount());
        paymentTransaction.setCurrency(order.getPaymentMethodId().getCurrency());
        paymentTransaction.setStatus(Status.RETURNED);
        paymentTransaction.setTransactionDate(LocalDateTime.now());
        paymentTransaction.setProviderResponse("Order cancelled successfully. Order id: " + orderId);
        paymentTransactionRepository.save(paymentTransaction);
    }

    private Integer generateTransactionId() {
        return (int) (Math.random() * 900000) + 100000;
    }

    @Override
    public ApiResponse<List<PaymentTransactionResponse>> getPaymentTransaction(Long paymentMethodId) {
        log.info("Actionlog.getPaymentTransaction.start : ");
        var userId = getCurrentUserId();
        var payment = paymentMethodRepository.findById(paymentMethodId).orElseThrow(() -> new NotFoundException("PaymentMethod not found with id: " + paymentMethodId));
        if (!payment.getUserId().getId().equals(userId)) {
            throw new NotFoundException("PaymentMethod not found with id: " + paymentMethodId);
        }
        var paymentTransactions = paymentTransactionRepository.findByPayment(payment);
        if (paymentTransactions.isEmpty()) {
            throw new NotFoundException("PaymentTransaction not found with payment id: " + paymentMethodId);
        }
        var responses = paymentTransactions.stream()
                .map(t -> PaymentTransactionResponse.builder()
                        .providerResponse(t.getProviderResponse())
                        .amount(t.getAmount())
                        .currency(t.getCurrency())
                        .id(t.getId())
                        .status(t.getStatus())
                        .transactionDate(t.getTransactionDate())
                        .transactionId(t.getTransactionId())
                        .paymentMethodId(t.getPayment().getId())
                        .orderId(t.getOrder() != null ? t.getOrder().getId() : null)
                        .maskedCardNumber(t.getPayment().getCardNumber())
                        .build())
                .toList();

        log.info("Actionlog.getPaymentTransaction.end : ");
        return ApiResponse.<List<PaymentTransactionResponse>>builder()
                .status(200)
                .message("Transactions retrieved successfully")
                .data(responses)
                .build();
    }


    @Override
    public ApiResponse<List<TransactionResponse>> getAllTransactions() {
        log.info("Actionlog.getAllTransactions.start : ");

        var userId = getCurrentUserId();
        var paymentMethod = paymentMethodRepository.findByUserId_IdAndIsDefault(userId, true).orElseThrow(() -> new NotFoundException("PaymentMethod not found with id: " + userId));
        var transactions = transactionClient.getAllTransactions(paymentMethod.getCardNumber());
        if (transactions.isEmpty()) {
            throw new NotFoundException("Transactions not found");
        }
        log.info("Actionlog.getAllTransactions.end : ");
        return ApiResponse.<List<TransactionResponse>>builder()
                .status(200)
                .message("All transactions retrieved successfully")
                .data(transactions)
                .build();
    }


    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }


}
