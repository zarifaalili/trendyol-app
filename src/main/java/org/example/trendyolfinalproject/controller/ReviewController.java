package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.projection.NegativeReviewProjection;
import org.example.trendyolfinalproject.model.request.ReviewCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ReviewResponse;
import org.example.trendyolfinalproject.model.response.TopRatedProductResponse;
import org.example.trendyolfinalproject.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<String>>  createReview(@RequestBody @Valid ReviewCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(request));
    }

    @GetMapping("/{productId}/average")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable Long productId) {
        return ResponseEntity.ok().body(reviewService.getAverageRating(productId));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByProductId(
            @PathVariable("productId") Long productId) {
        return ResponseEntity.ok().body(reviewService.getreviews(productId));
    }


    @GetMapping("/top-rated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TopRatedProductResponse>>> getTopRatedProducts() {
        return ResponseEntity.ok().body(reviewService.getTopRatedProducts());
    }


    @GetMapping("/user")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getUserReviews() {
        return ResponseEntity.ok().body(reviewService.getUserReviews());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getUserReviewsByAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok().body(reviewService.getUserReviewsByAdmin(userId));
    }

    @GetMapping("/negative")
    public ResponseEntity<ApiResponse<List<NegativeReviewProjection>>> getNegativeReview() {
        return ResponseEntity.ok().body(reviewService.getNegativeReview());
    }


    @GetMapping("/{productId}/filter")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviewsWithFilter(@PathVariable Long productId,
                                                                         @RequestParam(required = false) Integer[] rating, @RequestParam(required = false) String subject) {
        return ResponseEntity.ok().body(reviewService.getProductReviewsWithFilter(productId, rating, subject));
    }

    @PatchMapping("/approve/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> approve(@PathVariable Long reviewId) {
        reviewService.approveReview(reviewId);
        return ResponseEntity.ok().body(ApiResponse.success("Approved"));
    }

}
