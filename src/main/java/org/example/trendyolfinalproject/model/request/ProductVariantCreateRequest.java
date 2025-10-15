package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantCreateRequest {

    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @NotBlank(message = "Color cannot be blank")
    private String color;

    @NotNull(message = "Attribute value cannot be null")
    private String attributeValue1;

    @NotNull(message = "Size cannot be null")
    private String size;

    @NotNull(message = "Attribute value cannot be null")
    private String attributeValue2;

    @NotNull(message = "Stock quantity cannot be null")
    private Integer stockQuantity;

    @NotNull(message = "Price cannot be null")
    private String sku;


}
