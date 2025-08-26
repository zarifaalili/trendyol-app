package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {


    private Long id;
    private Long orderId;
    private Long trackingNumber;
    private String carrierName;
    private BigDecimal shippingCost;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
