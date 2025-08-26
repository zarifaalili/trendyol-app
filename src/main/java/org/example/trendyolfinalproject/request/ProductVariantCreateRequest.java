package org.example.trendyolfinalproject.request;

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

    private String attributeValue1;

    private String size;

    private String attributeValue2;

    private Integer stockQuantity;

    private String sku;

//    @ElementCollection(targetClass = String.class)
//    private List<String> imageUrls;


}
