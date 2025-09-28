package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDetailResponse {
    private Long id;
    private Long productId;
    private String color;
    private String size;
    private String attributeValue1;
    private String attributeValue2;
    private Integer stockQuantity;
    private String sku;
    private Double price;
    private Double previousPrice;

    private List<String> imageUrls;     // şəkil linkləri
    private List<String> imageBase64s;  // şəkillərin base64 formatı
}

