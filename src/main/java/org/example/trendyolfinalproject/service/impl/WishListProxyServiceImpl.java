package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.WishlistRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.WishListMapper;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.WishListResponse;
import org.example.trendyolfinalproject.service.WishListProxyService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishListProxyServiceImpl implements WishListProxyService {

    private final WishlistRepository wishListRepository;
    private final WishListMapper wishListMapper;

    @Override
    public ApiResponse<WishListResponse> getWishListForUser(Long wishListId, User currentUser) {
        log.info("Actionlog.getWishListForUser.start : ");
        var wishList = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new NotFoundException("WishList not found"));
        if (!wishList.getUser().equals(currentUser) &&
                !wishList.getSharedWith().contains(currentUser)) {
            throw new AccessDeniedException("Access denied: You cannot view this wishlist");
        }

        var mapper = wishListMapper.toResponse(wishList);
        mapper.setAddedAt(wishList.getAddedAt());

        return ApiResponse.<WishListResponse>builder()
                .data(mapper)
                .message("Success")
                .status(200)
                .build();
    }

}

