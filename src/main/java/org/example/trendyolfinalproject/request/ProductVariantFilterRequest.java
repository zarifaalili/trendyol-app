package org.example.trendyolfinalproject.request;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductVariantFilterRequest {
    private String color;
    private String size;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}

