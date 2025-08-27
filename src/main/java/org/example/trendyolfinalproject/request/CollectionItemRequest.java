package org.example.trendyolfinalproject.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionItemRequest {

    @NotNull(message = "Collection ID cannot be null")
    private Long collectionId;
    @NotNull(message = "ProductVariant ID cannot be null")
    private Long productVariantId;

}