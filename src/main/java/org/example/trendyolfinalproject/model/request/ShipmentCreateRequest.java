package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreateRequest {

    @NotNull(message = "Order ID cannot be null")
    private Long orderId;

    @NotNull(message = "Tracking number cannot be null")
    private Long trackingNumber;

    @NotBlank(message = "Carrier name cannot be blank")
    private String carrierName;

    @NotNull(message = "Shipping cost cannot be null")
    @DecimalMin(value = "0.00", message = "Shipping cost cannot be negative")
    private BigDecimal shippingCost;

    @NotNull(message = "Estimated delivery date cannot be null")
    @FutureOrPresent(message = "Estimated delivery date must be in the future or present")
    private LocalDateTime estimatedDeliveryDate;

    @NotNull(message = "Status cannot be null")
    private Status status;

}
