package org.example.trendyolfinalproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.WishListCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.WishListResponse;
import org.example.trendyolfinalproject.service.WishListProxyService;
import org.example.trendyolfinalproject.service.WishListService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<WishListResponse>> createWishList(@RequestBody @Valid WishListCreateRequest request) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishListService.addToFavorite(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deleteWishList(@PathVariable Long id) {
        wishListService.deleteFromFavorites(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<WishListResponse>>> getWishList() throws JsonProcessingException {
        return ResponseEntity.ok().body(wishListService.getFavorites());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WishListResponse>>> serchWishList(@RequestParam String productName) {
        return ResponseEntity.ok().body(wishListService.serchWishList(productName));
    }

    @GetMapping("/decreased-cost")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<WishListResponse>>> getProductVariantsByDecreasedCost() {
        return ResponseEntity.ok().body(wishListService.getProductVariantsByDecreasedCost());
    }


    @PostMapping("/{wishListId}/share/{userId}")
    public ResponseEntity<ApiResponse<String>> shareWithUser(
            @PathVariable Long wishListId,
            @PathVariable Long userId) {

        wishListService.shareWishListWithUser(wishListId, userId);
        return ResponseEntity.ok().body(ApiResponse.success("WishList shared successfully"));
    }

    @GetMapping("/{wishListId}/view")
    public ResponseEntity<ApiResponse<WishListResponse>> viewWishList(@PathVariable Long wishListId) {
        var currentUser = wishListService.getCurrentUser();
        var wishList = wishListProxyService.getWishListForUser(wishListId, currentUser);
        return ResponseEntity.ok().body(wishList);
    }


}
