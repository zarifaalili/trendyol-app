package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.projection.NegativeReviewProjection;
import org.example.trendyolfinalproject.request.ReviewCreateRequest;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.example.trendyolfinalproject.response.ReviewResponse;
import org.example.trendyolfinalproject.response.TopRatedProductResponse;
import org.example.trendyolfinalproject.service.ReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/createReview")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String>  createReview(@RequestBody @Valid ReviewCreateRequest request) {
        return reviewService.createReview(request);
    }

    @GetMapping("/getAverageRating/{productId}")
    public ApiResponse<Double> getAverageRating(@PathVariable Long productId) {
        return reviewService.getAverageRating(productId);
    }

    @GetMapping("/getReviewsByProductId/{productId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByProductId(
            @PathVariable("productId") Long productId) {
        return reviewService.getreviews(productId);
    }


    @GetMapping("/getTopRatedProducts")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<TopRatedProductResponse>> getTopRatedProducts() {
        return reviewService.getTopRatedProducts();
    }


    @GetMapping("/getUserReviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<ReviewResponse>> getUserReviews() {
        return reviewService.getUserReviews();
    }

    @GetMapping("/getUserReviewsByAdmin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReviewResponse>> getUserReviewsByAdmin(@PathVariable Long userId) {
        return reviewService.getUserReviewsByAdmin(userId);
    }

    @GetMapping("/getNegativeReview")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<NegativeReviewProjection>> getNegativeReview() {
        return reviewService.getNegativeReview();
    }


    @GetMapping("/getReviewsWithFilter/{productId}")
    public ApiResponse<List<ReviewResponse>> getProductReviewsWithFilter(@PathVariable Long productId,
                                                                         @RequestParam(required = false) Integer[] rating, @RequestParam(required = false) String subject) {
        return reviewService.getProductReviewsWithFilter(productId, rating, subject);
    }
//    @DeleteMapping("/deleteReview/{id}")
//    public void deleteReview(@PathVariable Long id) {
//        reviewService.deleteReview(id);
//    }


}
