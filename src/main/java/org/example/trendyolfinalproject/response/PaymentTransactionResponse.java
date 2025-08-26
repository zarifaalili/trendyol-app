package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.Currency;
import org.example.trendyolfinalproject.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionResponse {
    private Long id;
    private Long orderId;
    private Long paymentMethodId;
    private String maskedCardNumber;
    private Integer transactionId;
    private BigDecimal amount;
    private Currency currency;
    private Status status;
    private LocalDateTime transactionDate;
    private String providerResponse;
}
