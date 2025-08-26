package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.response.ProductVariantResponse;
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
    public void saveView(@PathVariable Long userId, @PathVariable Long productId) {
        var product = productVariantService.getProductVariant(productId);
        recommendationService.saveUserView(userId, product.getId());
    }


    @GetMapping("/personalized")
    public List<ProductVariantResponse> getUserRecommendations() {
        return productVariantService.getUserRecommendations();
    }

    @GetMapping("/similar/{productId}")
    public List<ProductVariantResponse> getSimilarProducts(@PathVariable Long productId) {
        return recommendationService.getSimilarProduct(productId);
    }


    @GetMapping("/trending")
    public List<ProductVariantResponse> getTrendingProducts() {
        return recommendationService.getTrendingProducts();
    }
}
