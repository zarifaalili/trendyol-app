package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponResponse {

    private Long id;
    private Long userId;
    private Long couponId;
    private String couponCode;
    private String couponName;
    private BigDecimal usageCount;
    private LocalDateTime lastUsedDate;

}
