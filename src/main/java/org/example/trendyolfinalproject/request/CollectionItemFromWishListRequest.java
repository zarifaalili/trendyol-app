package org.example.trendyolfinalproject.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectionItemFromWishListRequest {
    @NotNull(message = "Collection ID cannot be null")
    private Long collectionId;
    @NotNull(message = "Wish list ID cannot be null")
    private Long wishListId;
}
