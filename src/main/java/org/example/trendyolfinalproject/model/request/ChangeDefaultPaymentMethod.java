package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDefaultPaymentMethod {
    @NotNull(message = "Payment method ID cannot be null")
    private Long paymentMethodId;
}
