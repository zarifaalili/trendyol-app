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

    @NotNull(message = "Collection cannot be null")
    private Long collectionId;
    @NotNull(message = "ProductVariant cannot be null")
    private Long productVariantId;

}