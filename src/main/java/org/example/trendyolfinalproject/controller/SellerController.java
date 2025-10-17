package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.SellerCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SellerFollowResponse;
import org.example.trendyolfinalproject.model.response.SellerResponse;
import org.example.trendyolfinalproject.service.SellerFollowService;
import org.example.trendyolfinalproject.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final SellerFollowService sellerFollowService;

    @PostMapping
    public ResponseEntity<ApiResponse<SellerResponse>> createSeller(@RequestBody @Valid SellerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerService.createSeller(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SellerResponse>>> getSellers() {
        return ResponseEntity.ok().body(sellerService.getSellers());
    }


    @GetMapping("/{companyName}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<ApiResponse<SellerResponse>> getSeller(@PathVariable String companyName) {
        return ResponseEntity.ok().body(sellerService.getSeller(companyName));
    }


    @PostMapping("/{sellerId}/follow")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> follow(@PathVariable Long sellerId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerFollowService.follow(sellerId));
    }


    @GetMapping("/followers")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<SellerFollowResponse>>> getAllFollowers() {
        return ResponseEntity.ok().body(sellerFollowService.getAllFollowers());
    }


    @GetMapping("/{sellerId}/raiting")
    public ResponseEntity<ApiResponse<Double>> getRaiting(@PathVariable Long sellerId) {
        return ResponseEntity.ok().body(sellerService.getSellerAverageRating(sellerId));
    }

    @DeleteMapping("/{sellerId}/unfollow")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> unfollowSeller(@PathVariable Long sellerId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(sellerFollowService.unfollow(sellerId));
    }
}
