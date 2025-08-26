package org.example.trendyolfinalproject.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectionItemFromWishListRequest {
    private Long collectionId;
    private Long wishListId;
}
