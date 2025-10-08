package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.CollectionCreateRequest;
import org.example.trendyolfinalproject.model.request.CollectionItemFromWishListRequest;
import org.example.trendyolfinalproject.model.request.CollectionItemRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CollectionItemFromWishListResponse;
import org.example.trendyolfinalproject.model.response.CollectionItemResponse;
import org.example.trendyolfinalproject.model.response.CollectionResponse;

import java.util.List;

public interface CollectionService {

    ApiResponse<CollectionResponse> createCollection(CollectionCreateRequest request);

    ApiResponse<CollectionItemResponse> addProductToCollection(CollectionItemRequest request);

    ApiResponse<CollectionItemFromWishListResponse> addProductToCollectionFromWishList(CollectionItemFromWishListRequest request);

    ApiResponse<List<CollectionResponse>> getAllCollections();

    ApiResponse<Void> shareCollection(Long collectionId, Long targetUserId);

    ApiResponse<CollectionResponse> readSharedCollection(Long collectionId, Long userId);

    ApiResponse<CollectionResponse> renameCollection(Long collectionId, String newName);

    Long getCurrentUserId();



}
