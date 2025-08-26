package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishListResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long productVariantId;
    private String productName;
    private String productImageUrl;
    private LocalDateTime addedAt;
    private BigDecimal productPrice;
    private BigDecimal previousPrice;


}
