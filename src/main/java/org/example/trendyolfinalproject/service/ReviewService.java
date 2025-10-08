package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.ReviewCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ReviewResponse;
import org.example.trendyolfinalproject.model.response.TopRatedProductResponse;
import org.example.trendyolfinalproject.projection.NegativeReviewProjection;

import java.util.List;

public interface ReviewService {

    ApiResponse<String> createReview(ReviewCreateRequest request);

    ApiResponse<Double> getAverageRating(Long productId);

    ApiResponse<List<ReviewResponse>> getreviews(Long productId);

    ApiResponse<List<TopRatedProductResponse>> getTopRatedProducts();

    ApiResponse<List<ReviewResponse>> getUserReviews();

    ApiResponse<List<ReviewResponse>> getUserReviewsByAdmin(Long userId);

    ApiResponse<List<NegativeReviewProjection>> getNegativeReview();

    ApiResponse<List<ReviewResponse>> getProductReviewsWithFilter(Long productId, Integer[] rating, String subject);



}
