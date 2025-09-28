package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.CollectionCreateRequest;
import org.example.trendyolfinalproject.model.request.CollectionItemFromWishListRequest;
import org.example.trendyolfinalproject.model.request.CollectionItemRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CollectionItemFromWishListResponse;
import org.example.trendyolfinalproject.model.response.CollectionItemResponse;
import org.example.trendyolfinalproject.model.response.CollectionResponse;
import org.example.trendyolfinalproject.service.CollectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/collections")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;


    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<CollectionResponse> createCollection(@RequestBody @Valid CollectionCreateRequest request) {
        return collectionService.createCollection(request);
    }

    @PostMapping("/items/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<CollectionItemResponse> addProductToCollection(@RequestBody @Valid CollectionItemRequest request) {
        return collectionService.addProductToCollection(request);
    }


    @PostMapping("/items/add/from-wishlist")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<CollectionItemFromWishListResponse> addProductToCollectionFromWishList(@RequestBody @Valid CollectionItemFromWishListRequest request) {
        return collectionService.addProductToCollectionFromWishList(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<List<CollectionResponse>> getAllCollections() {
        return collectionService.getAllCollections();
    }

    @PostMapping("/{collectionId}/share/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> shareCollection(
            @PathVariable Long collectionId,
            @PathVariable Long targetUserId) {
        var response = collectionService.shareCollection(collectionId, targetUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shared/{collectionId}")
    public ResponseEntity<ApiResponse<CollectionResponse>> readSharedCollection(
            @PathVariable Long collectionId) {

        Long currentUserId =collectionService.getCurrentUserId();

        var response = collectionService.readSharedCollection(collectionId, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{collectionId}/rename")
    public ResponseEntity<ApiResponse<CollectionResponse>> renameCollection(@PathVariable Long collectionId, @PathParam("newName") String newName) {
        var response = collectionService.renameCollection(collectionId, newName);
        return ResponseEntity.ok(response);    }
}
