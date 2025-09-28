package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.WishListCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.WishListResponse;
import org.example.trendyolfinalproject.service.WishListProxyService;
import org.example.trendyolfinalproject.service.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/wishLists")
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;
    private final WishListProxyService wishListProxyService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<WishListResponse> createWishList(@RequestBody @Valid WishListCreateRequest request) {
        return wishListService.addToFavorite(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public void deleteWishList(@PathVariable Long id) {
        wishListService.deleteFromFavorites(id);
    }


    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<WishListResponse>> getWishList() {
        return wishListService.getFavorites();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<WishListResponse>> serchWishList(@RequestParam String productName) {
        return wishListService.serchWishList(productName);
    }

    @GetMapping("/decreased-cost")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<WishListResponse>> getProductVariantsByDecreasedCost() {
        return wishListService.getProductVariantsByDecreasedCost();
    }


    @PostMapping("/{wishListId}/share/{userId}")
    public ResponseEntity<String> shareWithUser(
            @PathVariable Long wishListId,
            @PathVariable Long userId) {

        wishListService.shareWishListWithUser(wishListId, userId);
        return ResponseEntity.ok("WishList shared with user successfully");
    }

    @GetMapping("/{wishListId}/view")
    public ApiResponse<WishListResponse> viewWishList(@PathVariable Long wishListId) {
        var currentUser = wishListService.getCurrentUser();
        var wishList = wishListProxyService.getWishListForUser(wishListId, currentUser);
        return wishList;
    }


}
