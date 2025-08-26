package org.example.trendyolfinalproject.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDefaultPaymentMethod {
//    private Long userId;
    private Long paymentMethodId;
}
