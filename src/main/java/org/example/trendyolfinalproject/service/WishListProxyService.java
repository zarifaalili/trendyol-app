package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.WishListResponse;

public interface WishListProxyService {

    ApiResponse<WishListResponse> getWishListForUser(Long wishListId, User currentUser);

}
