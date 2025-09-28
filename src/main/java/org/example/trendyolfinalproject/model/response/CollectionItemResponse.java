package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionItemResponse {

    private Long id;
    private Long productVariantId;
    private String productName;
    private LocalDateTime addedAt;
}
