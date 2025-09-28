package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponCreateRequest {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Coupon ID cannot be null")
    private Long couponId;

    @NotNull(message = "Usage count cannot be null")
    @Min(value = 0, message = "Usage count must be a non-negative value")
    private BigDecimal usageCount;

    private LocalDateTime lastUsedDate;


}
