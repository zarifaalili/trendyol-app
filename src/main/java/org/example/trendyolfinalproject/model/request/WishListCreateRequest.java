package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishListCreateRequest {

//    @NotNull(message = "User ID cannot be null")
//    private Long userId;

    @NotNull(message = "Product ID cannot be null")
    private Long productVariantId;
}
