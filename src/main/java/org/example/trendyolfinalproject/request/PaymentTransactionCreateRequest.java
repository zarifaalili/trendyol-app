package org.example.trendyolfinalproject.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.Currency;
import org.example.trendyolfinalproject.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionCreateRequest {

    @NotNull(message = "Order ID cannot be null")
    private Long orderId;

    @NotNull(message = "Payment method ID cannot be null")
    private Long paymentMethodId;

    @NotNull(message = "Transaction ID cannot be null")
    private Integer transactionId;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Currency cannot be null")
    private Currency currency;

    @NotNull(message = "Status cannot be null")
    private Status status;

    @NotNull(message = "Transaction date cannot be null")
    private LocalDateTime transactionDate;

    private String providerResponse;
}
