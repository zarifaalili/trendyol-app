package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CollectionItemFromWishListResponse {
    private Long id;
    private Long productVariantId;
    private String productName;
    private LocalDateTime addedAt;
}
