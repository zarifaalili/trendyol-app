package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CouponCreateRequest {

    @NotBlank(message = "Coupon code cannot be blank")
    private String code;

    @NotNull(message = "Discount type cannot be null")
    private DiscountType discountType;

    @NotNull(message = "Discount value cannot be null")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.00", message = "Minimum order amount cannot be negative")
    private BigDecimal minimumOrderAmount;

    @DecimalMin(value = "0.00", message = "Maximum discount amount cannot be negative")
    private BigDecimal maximumDiscountAmount;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @FutureOrPresent(message = "End date must be in the future or present")
    private LocalDateTime endDate;

    private Integer usageLimit;

    @Min(value = 0, message = "Per user limit cannot be negative")
    private Integer perUserLimit;

    @NotNull(message = "Active status cannot be null")
    private Boolean isActive;

    @NotNull(message = "First order only cannot be null")
    private Boolean firstOrderOnly;

    @NotNull(message = "Min order count cannot be null")
    private Integer minOrderCount;


}
