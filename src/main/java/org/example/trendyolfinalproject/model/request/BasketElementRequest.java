package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BasketElementRequest {

    @NotNull(message = "Product Variant ID cannot be null")
    private Long productVariantId;
}
