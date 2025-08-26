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
public class OrderResponse {

    private Long id;
    private Long userId;
    private String userName;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private Long shippingAddressId;
    private String shippingAddressDetails;
    private Long billingAddressId;
    private String billingAddressDetails;
    private Long paymentMethodId;
    private String paymentMethodName;
    private Status status;
    private Integer trackingNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;




}
