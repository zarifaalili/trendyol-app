package org.example.trendyolfinalproject.request;

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

//    @NotNull(message = "User ID cannot be null")
//    private Long userId;

//    @NotNull(message = "Total amount cannot be null")
//    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
//    private BigDecimal totalAmount;

    @NotNull(message = "Shipping address ID cannot be null")
    private Long shippingAddressId;

    @NotNull(message = "Billing address ID cannot be null")
    private Long billingAddressId;

//    @NotNull(message = "Payment method ID cannot be null")
//    private Long paymentMethodId;


}
