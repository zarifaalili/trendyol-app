package org.example.trendyolfinalproject.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopRatedProductResponse {
    private Long productId;
    private String productName;
    private Double avgRating;
}
