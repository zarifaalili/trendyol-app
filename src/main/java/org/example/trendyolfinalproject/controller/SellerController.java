package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.SellerCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SellerFollowResponse;
import org.example.trendyolfinalproject.model.response.SellerResponse;
import org.example.trendyolfinalproject.service.SellerFollowService;
import org.example.trendyolfinalproject.service.SellerService;
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
    public ApiResponse<SellerResponse> createSeller(@RequestBody @Valid SellerCreateRequest request) {
        return sellerService.createSeller(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SellerResponse>> getSellers() {
        return sellerService.getSellers();
    }


    @GetMapping("/{companyName}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ApiResponse<SellerResponse> getSeller(@PathVariable String companyName) {
        return sellerService.getSeller(companyName);
    }


    @PostMapping("/{sellerId}/follow")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> follow(@PathVariable Long sellerId) {
        return sellerFollowService.follow(sellerId);
    }

    @GetMapping("/followers")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<List<SellerFollowResponse>> getAllFollowers() {
        return sellerFollowService.getAllFollowers();
    }

    @GetMapping("/{sellerId}/raiting")
    public ApiResponse<Double> getRaiting(@PathVariable Long sellerId) {
        return sellerService.getSellerAverageRating(sellerId);
    }
}
