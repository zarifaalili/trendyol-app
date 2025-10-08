package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.WishlistRepository;
import org.example.trendyolfinalproject.mapper.WishListMapper;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.WishListResponse;
import org.example.trendyolfinalproject.service.WishListProxyService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishListProxyServiceImpl implements WishListProxyService {

    private final WishlistRepository wishListRepository;
    private final WishListMapper  wishListMapper;



    @Override
    public ApiResponse<WishListResponse> getWishListForUser(Long wishListId, User currentUser) {

        var wishList = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new RuntimeException("WishList not found"));

        if (!wishList.getUser().equals(currentUser) &&
                !wishList.getSharedWith().contains(currentUser)) {
            throw new RuntimeException("Access denied: You cannot view this wishlist");
        }


        var mapper=wishListMapper.toResponse(wishList);
        mapper.setAddedAt(wishList.getAddedAt());


        return ApiResponse.<WishListResponse>builder()
                .data(mapper)
                .message("Success")
                .status(200)
                .build();
    }

}

