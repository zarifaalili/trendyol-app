package org.example.trendyolfinalproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.request.WishListCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.WishListResponse;

import java.util.List;

public interface WishListService {

    ApiResponse<WishListResponse> addToFavorite(WishListCreateRequest request) throws JsonProcessingException;

    ApiResponse<Void> deleteFromFavorites(Long id);

    ApiResponse<List<WishListResponse>> getFavorites() throws JsonProcessingException;

    ApiResponse<List<WishListResponse>> serchWishList(String productName);

    ApiResponse<List<WishListResponse>> getProductVariantsByDecreasedCost();

    ApiResponse<Void> shareWishListWithUser(Long wishListId, Long userId);

    User getCurrentUser();
}
