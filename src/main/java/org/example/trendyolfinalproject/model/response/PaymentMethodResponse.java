package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.CardType;
import org.example.trendyolfinalproject.model.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodResponse {

    private Long id;
    private Long userId;
    private CardType cardType;
    private String maskedCardNumber;
    private LocalDateTime expirationDate;
    private String cardHolderName;
    private Boolean isDefault;
    private Currency currency;
    private BigDecimal balance;

}
