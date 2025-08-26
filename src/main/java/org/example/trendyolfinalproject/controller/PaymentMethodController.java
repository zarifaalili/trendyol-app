package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.AddBalanceRequest;
import org.example.trendyolfinalproject.request.ChangeDefaultPaymentMethod;
import org.example.trendyolfinalproject.request.PaymentMethodCreateRequest;
import org.example.trendyolfinalproject.request.PaymentRequest;
import org.example.trendyolfinalproject.response.PaymentMethodResponse;
import org.example.trendyolfinalproject.response.PaymentResponse;
import org.example.trendyolfinalproject.response.PaymentTransactionResponse;
import org.example.trendyolfinalproject.response.TransactionResponse;
import org.example.trendyolfinalproject.service.PaymentMethodService;
import org.example.trendyolfinalproject.service.PaymentTransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/payment-method")
@RequiredArgsConstructor
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;
    private final PaymentTransactionService paymentTransactionService;


    @PostMapping("/createPaymentMethod")
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    PaymentMethodResponse createPaymentMethod(@RequestBody @Valid PaymentMethodCreateRequest request) {
        return paymentMethodService.createPaymentMethod(request);
    }

    @PostMapping("/addbalance")
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    public void addbalance(@RequestBody AddBalanceRequest request) {
        paymentMethodService.addbalance(request);
    }

    @PostMapping("/changeDefaultPaymentMethod")
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    public PaymentMethodResponse changeDefaultPaymentMethod(@RequestBody ChangeDefaultPaymentMethod request) {
        return paymentMethodService.changeDefaultPaymentMethod(request);
    }

    @GetMapping("/transaction/{paymentMethodId}")
    public List<PaymentTransactionResponse> getTransactions(@PathVariable Long paymentMethodId) {
        return paymentTransactionService.getPaymentTransaction(paymentMethodId);

    }

    @PostMapping("/pay")
    public PaymentResponse pay(@RequestBody @Valid PaymentRequest request) {
        return paymentMethodService.pay(request);
    }

    @GetMapping("/transactions/all")
    public List<TransactionResponse> getAllTransactions() {
        return paymentTransactionService.getAllTransactions();
    }

}
