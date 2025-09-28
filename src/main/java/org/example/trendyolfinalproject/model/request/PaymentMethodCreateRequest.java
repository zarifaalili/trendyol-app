package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.CardType;
import org.example.trendyolfinalproject.model.enums.Currency;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodCreateRequest {

//    @NotNull(message = "User ID cannot be null")
//    private Long userId;

    @NotNull(message = "Card type cannot be null")
    private CardType cardType;

    @NotBlank(message = "Card number cannot be blank")
//    @Pattern(regexp = "\\d{4}", message = "Card number must be last 4 digits")
    @Size(min=16,max=16,message = "Card number must be 16 digits")
    private String cardNumber;

    @NotNull(message = "Expiration date cannot be null")
    @FutureOrPresent(message = "Expiration date must be in the future or present")
    private LocalDateTime expirationDate;

    @NotBlank(message = "Card holder name cannot be blank")
    private String cardHolderName;

//    @NotNull(message = "Is default status cannot be null")
//    private Boolean isDefault;

    @NotNull(message = "Currency cannot be null")
    private Currency currency;

//    private BigDecimal balance;

}
