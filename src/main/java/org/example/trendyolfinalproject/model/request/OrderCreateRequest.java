package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "Shipping address ID cannot be null")
    private Long shippingAddressId;

    @NotNull(message = "Billing address ID cannot be null")
    private Long billingAddressId;

}
