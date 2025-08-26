package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.WishListCreateRequest;
import org.example.trendyolfinalproject.response.WishListResponse;
import org.example.trendyolfinalproject.service.WishListProxyService;
import org.example.trendyolfinalproject.service.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/wishList")
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;
    private final WishListProxyService wishListProxyService;

    @PostMapping("/createWishList")
    @PreAuthorize("hasRole('CUSTOMER')")
    WishListResponse createWishList(@RequestBody WishListCreateRequest request) {
        return wishListService.addToFavorite(request);
    }

    @DeleteMapping("/deleteWishList/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public void deleteWishList(@PathVariable Long id) {
        wishListService.deleteFromFavorites(id);
    }


    @GetMapping("/getWishList")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<WishListResponse> getWishList() {
        return wishListService.getFavorites();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<WishListResponse> serchWishList(@RequestParam String productName) {
        return wishListService.serchWishList(productName);
    }

    @GetMapping("/decreasedCost")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<WishListResponse> getProductVariantsByDecreasedCost() {
        return wishListService.getProductVariantsByDecreasedCost();
    }


    @PostMapping("/shareWithUser/{wishListId}/{userId}")
    public ResponseEntity<String> shareWithUser(
            @PathVariable Long wishListId,
            @PathVariable Long userId) {

        wishListService.shareWishListWithUser(wishListId, userId);
        return ResponseEntity.ok("WishList shared with user successfully");
    }

    @GetMapping("/view/{wishListId}")
    public ResponseEntity<WishListResponse> viewWishList(@PathVariable Long wishListId) {
        var currentUser = wishListService.getCurrentUser();
        var wishList = wishListProxyService.getWishListForUser(wishListId, currentUser);
        return ResponseEntity.ok(wishList);
    }


}
