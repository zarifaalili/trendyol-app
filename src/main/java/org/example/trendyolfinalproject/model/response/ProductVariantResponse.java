package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {

    private Long id;
    private Long productId;
    private String color;
    private String attributeValue1;
    private String size;
    private String attributeValue2;
    private Integer stockQuantity;
    private String sku;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal previousPrice;

}
