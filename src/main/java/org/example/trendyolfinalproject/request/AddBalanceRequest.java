package org.example.trendyolfinalproject.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBalanceRequest {
    @Size(min = 16, max = 16)
    @NotNull(message = "Card number cannot be null")
    private String cardNumber;
    @Min(1)
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
}
