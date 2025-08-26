package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.SellerCreateRequest;
import org.example.trendyolfinalproject.response.SellerFollowResponse;
import org.example.trendyolfinalproject.response.SellerResponse;
import org.example.trendyolfinalproject.service.SellerFollowService;
import org.example.trendyolfinalproject.service.SellerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final SellerFollowService sellerFollowService;

    @PostMapping("/createSeller")
    public SellerResponse createSeller(@RequestBody SellerCreateRequest request) {
        return sellerService.createSeller(request);
    }

    @GetMapping("/getSellers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SellerResponse> getSellers() {
        return sellerService.getSellers();
    }


    @GetMapping("/getSeller/{companyName}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public SellerResponse getSeller(@PathVariable String companyName) {
        return sellerService.getSeller(companyName);
    }


    @PostMapping("/follow/{sellerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String follow(@PathVariable Long sellerId) {
        return sellerFollowService.follow(sellerId);
    }

    @GetMapping("/followers/all")
    @PreAuthorize("hasRole('SELLER')")
    public List<SellerFollowResponse> getAllFollowers() {
        return sellerFollowService.getAllFollowers();
    }
}
