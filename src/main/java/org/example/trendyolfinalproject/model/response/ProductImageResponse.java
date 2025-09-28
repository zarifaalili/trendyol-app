package org.example.trendyolfinalproject.model.response;

import lombok.Data;

@Data
public class ProductImageResponse {
    private Long id;
    private String imageUrl;
    private Boolean isMainImage;
    private Integer displayOrder;
    private String altText;
    private Long productId;
}
