package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantResponse;
import org.example.trendyolfinalproject.service.ProductVariantService;
import org.example.trendyolfinalproject.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ProductVariantService productVariantService;


    @PostMapping("/view/{userId}/{productId}")
    public ApiResponse<String> saveView(@PathVariable Long userId, @PathVariable Long productId) {
        var product = productVariantService.getProductVariant(productId);
        recommendationService.saveUserView(userId, product.getData().getProductId());
        return ApiResponse.success("View saved successfully");

    }


    @GetMapping("/personalized")
    public ApiResponse<List<ProductVariantResponse>> getUserRecommendations() {
        return productVariantService.getUserRecommendations();
    }

    @GetMapping("/similar/{productId}")
    public ApiResponse<List<ProductVariantResponse>> getSimilarProducts(@PathVariable Long productId) {
        return recommendationService.getSimilarProduct(productId);
    }


    @GetMapping("/trending")
    public ApiResponse<List<ProductVariantResponse>> getTrendingProducts() {
        return recommendationService.getTrendingProducts();
    }
}
