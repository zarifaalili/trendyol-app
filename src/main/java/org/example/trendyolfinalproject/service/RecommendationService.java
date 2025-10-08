package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantResponse;

import java.util.List;

public interface RecommendationService {

    ApiResponse<Void> saveUserView(Long userId, Long productVariantId);

    ApiResponse<List<Long>> getLastViewedIds(Long userId);

    ApiResponse<List<ProductVariantResponse>> getSimilarProduct(Long productId);

    ApiResponse<List<ProductVariantResponse>> getTrendingProducts();

}
