package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantResponse;
import org.example.trendyolfinalproject.service.ProductVariantService;
import org.example.trendyolfinalproject.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ProductVariantService productVariantService;


    @PostMapping("/view/{userId}/{productId}")
    public ResponseEntity<ApiResponse<String>> saveView(@PathVariable Long userId, @PathVariable Long productId) {
        var product = productVariantService.getProductVariant(productId);
        recommendationService.saveUserView(userId, product.getData().getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(null, "View saved"));
    }


    @GetMapping("/personalized")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getUserRecommendations() {
        return ResponseEntity.ok().body(productVariantService.getUserRecommendations());
    }

    @GetMapping("/similar/{productId}")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getSimilarProducts(@PathVariable Long productId) {
        return ResponseEntity.ok().body(recommendationService.getSimilarProduct(productId));
    }


    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getTrendingProducts() {
        return ResponseEntity.ok().body(recommendationService.getTrendingProducts());
    }
}
