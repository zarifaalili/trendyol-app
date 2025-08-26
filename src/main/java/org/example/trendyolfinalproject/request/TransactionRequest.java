package org.example.trendyolfinalproject.request;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import org.example.trendyolfinalproject.model.Currency;
import org.example.trendyolfinalproject.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionRequest {

    private Integer transactionId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime transactionDate;

    private String providerResponse;

    private String sender;

    private String  receiver;

}

