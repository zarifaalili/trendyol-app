package org.example.trendyolfinalproject.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

//    @NotNull(message = "Payment method ID cannot be null")
//    private Long paymentMethodId;

    @NotNull(message = "Card number cannot be null")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits")
    private String cardNumber;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount must be at least 1")
    private BigDecimal amount;


}
