package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.AddBalanceRequest;
import org.example.trendyolfinalproject.model.request.ChangeDefaultPaymentMethod;
import org.example.trendyolfinalproject.model.request.PaymentMethodCreateRequest;
import org.example.trendyolfinalproject.model.request.PaymentRequest;
import org.example.trendyolfinalproject.model.response.*;
import org.example.trendyolfinalproject.service.PaymentMethodService;
import org.example.trendyolfinalproject.service.PaymentTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;
    private final PaymentTransactionService paymentTransactionService;


    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> createPaymentMethod(@RequestBody @Valid PaymentMethodCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethodService.createPaymentMethod(request));
    }

    @PostMapping("/add-balance")
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    public ResponseEntity<ApiResponse<String>> addbalance(@RequestBody @Valid AddBalanceRequest request) {
        return ResponseEntity.ok().body(paymentMethodService.addbalance(request));
    }

    @PostMapping("/default")
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> changeDefaultPaymentMethod(@RequestBody @Valid ChangeDefaultPaymentMethod request) {
        return ResponseEntity.ok().body(paymentMethodService.changeDefaultPaymentMethod(request));
    }

    @GetMapping("/{paymentMethodId}/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(@PathVariable Long paymentMethodId) {
        return ResponseEntity.ok().body(paymentTransactionService.getPaymentTransaction(paymentMethodId));

    }

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(@RequestBody @Valid PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethodService.pay(request));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAllTransactions() {
        return ResponseEntity.ok().body(paymentTransactionService.getAllTransactions());
    }

}
