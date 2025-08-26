package org.example.trendyolfinalproject.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantSimpleResponse {

    private BigDecimal price;
    private BigDecimal previousPrice;
    private String color;
    private String attributeValue1;
    private String size;
    private String attributeValue2;
    private String imageUrl;
}
