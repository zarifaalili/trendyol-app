package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.projection.NegativeReviewProjection;
import org.example.trendyolfinalproject.model.request.ReviewCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ReviewResponse;
import org.example.trendyolfinalproject.model.response.TopRatedProductResponse;
import org.example.trendyolfinalproject.service.ReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String>  createReview(@RequestBody @Valid ReviewCreateRequest request) {
        return reviewService.createReview(request);
    }

    @GetMapping("/{productId}/average")
    public ApiResponse<Double> getAverageRating(@PathVariable Long productId) {
        return reviewService.getAverageRating(productId);
    }

    @GetMapping("/{productId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByProductId(
            @PathVariable("productId") Long productId) {
        return reviewService.getreviews(productId);
    }


    @GetMapping("/top-rated")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<TopRatedProductResponse>> getTopRatedProducts() {
        return reviewService.getTopRatedProducts();
    }


    @GetMapping("/user")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<ReviewResponse>> getUserReviews() {
        return reviewService.getUserReviews();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReviewResponse>> getUserReviewsByAdmin(@PathVariable Long userId) {
        return reviewService.getUserReviewsByAdmin(userId);
    }

    @GetMapping("/negative")
    public ApiResponse<List<NegativeReviewProjection>> getNegativeReview() {
        return reviewService.getNegativeReview();
    }


    @GetMapping("/{productId}/filter")
    public ApiResponse<List<ReviewResponse>> getProductReviewsWithFilter(@PathVariable Long productId,
                                                                         @RequestParam(required = false) Integer[] rating, @RequestParam(required = false) String subject) {
        return reviewService.getProductReviewsWithFilter(productId, rating, subject);
    }

    @PatchMapping("/approve/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approve(@PathVariable Long reviewId) {
        reviewService.approveReview(reviewId);
        return ApiResponse.success("Approved");
    }

}
