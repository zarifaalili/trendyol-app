package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.CollectionCreateRequest;
import org.example.trendyolfinalproject.request.CollectionItemFromWishListRequest;
import org.example.trendyolfinalproject.request.CollectionItemRequest;
import org.example.trendyolfinalproject.response.*;
import org.example.trendyolfinalproject.service.CollectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/collection")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;


    @PostMapping("/createCollection")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<CollectionResponse> createCollection(@RequestBody @Valid CollectionCreateRequest request) {
        return collectionService.createCollection(request);
    }

    @PostMapping("/addProductToCollection")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<CollectionItemResponse> addProductToCollection(@RequestBody @Valid CollectionItemRequest request) {
        return collectionService.addProductToCollection(request);
    }


    @PostMapping("/addProductToCollectionFromWishList")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<CollectionItemFromWishListResponse> addProductToCollectionFromWishList(@RequestBody @Valid CollectionItemFromWishListRequest request) {
        return collectionService.addProductToCollectionFromWishList(request);
    }

    @GetMapping("/getAllCollectionsOfUser")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<List<CollectionResponse>> getAllCollections() {
        return collectionService.getAllCollections();
    }

    @PostMapping("/share/{collectionId}/{targetUserId}")
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
