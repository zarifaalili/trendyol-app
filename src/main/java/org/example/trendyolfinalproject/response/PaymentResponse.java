package org.example.trendyolfinalproject.response;

import lombok.Builder;
import lombok.Data;
import org.example.trendyolfinalproject.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {


    private Long paymentMethodId;
    private BigDecimal amount;
    private String fromCardNumber;
    private String toCardNumber;
    private String holderName;
    private Status status;
    private LocalDateTime paymentDate;

}
