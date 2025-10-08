package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.ProductVariantCreateRequest;
import org.example.trendyolfinalproject.model.request.ProductVariantFilterRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantDetailResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductVariantService {

    ApiResponse<ProductVariantResponse> createProductVariant(ProductVariantCreateRequest request);

    ApiResponse<Void> deleteProductVariant(Long id);

    ApiResponse<ProductVariantResponse> getProductVariant(Long id);

    ApiResponse<ProductVariantDetailResponse> getProductVariantDetails(Long id);

    ApiResponse<List<ProductVariantResponse>> getUserRecommendations();

    ApiResponse<List<ProductVariantResponse>> getProductVariantsByFilter(ProductVariantFilterRequest filter);

    ApiResponse<ProductVariantResponse> addImages(Long variantId, List<MultipartFile> images);

    ApiResponse<ProductVariantResponse> updateProductVariantStock(Long productVariantId, Integer newStock);

}
