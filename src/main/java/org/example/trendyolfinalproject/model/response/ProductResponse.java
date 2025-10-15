package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private String brandName;
    private String sellerCompanyName;
    private Integer stockQuantity;
//    private List<String> imageUrls;
//    private String sku;
    private BigDecimal weight;
    private BigDecimal dimensions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Status status;
}
