package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.CollectionItemMapper;
import org.example.trendyolfinalproject.mapper.CollectionMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.request.CollectionCreateRequest;
import org.example.trendyolfinalproject.model.request.CollectionItemFromWishListRequest;
import org.example.trendyolfinalproject.model.request.CollectionItemRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CollectionItemFromWishListResponse;
import org.example.trendyolfinalproject.model.response.CollectionItemResponse;
import org.example.trendyolfinalproject.model.response.CollectionResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.CollectionService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionServiceImpl implements CollectionService {
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final CollectionMapper collectionMapper;
    private final CollectionItemRepository collectionItemRepository;
    private final CollectionItemMapper collectionItemMapper;
    private final WishlistRepository wishlistRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;


    @Override
    public ApiResponse<CollectionResponse> createCollection(CollectionCreateRequest request) {
        log.info("Actionlog.createCollection.start : request={}", request);


        Long userId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("userId");
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));


        var exists = collectionRepository.findByUser_IdAndName(user.getId(), request.getName()).orElse(null);
        if (exists != null) {
            throw new AlreadyException("Collection name already exists");
        }
        var entity = collectionMapper.toEntity(request);
        entity.setUser(user);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        entity.setName(request.getName());
        entity.generateShareToken();
        var saved = collectionRepository.save(entity);
        var response = collectionMapper.toResponse(saved);

        auditLogService.createAuditLog(user, "Create Collection", "Collection created successfully. Collection id: " + saved.getId());

        log.info("Actionlog.createCollection.end : request={}", request);
        return ApiResponse.<CollectionResponse>builder()
                .status(200)
                .message("Collection created successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<CollectionItemResponse> addProductToCollection(CollectionItemRequest request) {
        log.info("Actionlog.addProductToCollection.start : productVariantId={}", request.getProductVariantId());

        Long userId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("userId");
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        var collection = collectionRepository.findById(request.getCollectionId()).orElseThrow(() -> new RuntimeException("Collection not found with id: " + request.getCollectionId()));

        if (!collection.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to add product to this collection");
        }

        var collectionItem = collectionItemRepository.findByProductVariant_IdAndCollection_Id(request.getProductVariantId(), request.getCollectionId());


        if (collectionItem.isPresent()) {
            throw new AlreadyException("Product already exists in collection");
        }
        var productVariant = productVariantRepository.findById(request.getProductVariantId()).orElseThrow(() -> new RuntimeException("ProductVariant not found with id: " + request.getProductVariantId()));
        var entity = collectionItemMapper.toEntity(request);
        entity.setProductVariant(productVariant);
        entity.setCollection(collection);
        entity.setAddedAt(LocalDateTime.now());
        var saved = collectionItemRepository.save(entity);
        var response = collectionItemMapper.toResponse(saved);

        auditLogService.createAuditLog(collection.getUser(), "Add Product to Collection", "Product added to collection successfully. Product id: " + saved.getId());

        log.info("Actionlog.addProductToCollection.end : userId={}", request.getProductVariantId());
        return ApiResponse.<CollectionItemResponse>builder()
                .status(200)
                .message("Product added to collection successfully")
                .data(response)
                .build();
    }


    @Override
    public ApiResponse<CollectionItemFromWishListResponse> addProductToCollectionFromWishList(CollectionItemFromWishListRequest request) {
        log.info("Actionlog.addProductToCollectionFromWishList.start : wishListId={}", request.getWishListId());
        Long userId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("userId");
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));


        var wishListItem = wishlistRepository.findById(request.getWishListId()).orElseThrow(
                () -> new NotFoundException(("Favorite not found with id: " + request.getWishListId()))
        );
        var collection = collectionRepository.findById(request.getCollectionId()).orElseThrow(() -> new RuntimeException("Collection not found with id: " + request.getCollectionId()));

        if (!(collection.getUser().getId().equals(userId)
                && wishListItem.getUser().getId().equals(userId))) {
            throw new RuntimeException("You don't have permission to add product to this collection");
        }

        var productVariant = wishListItem.getProductVariant();
        var collectionItem = collectionItemRepository.findByProductVariant_IdAndCollection_Id(productVariant.getId(), request.getCollectionId()).orElse(null);
        if (collectionItem != null) {
            throw new AlreadyException("ProductVariant already exists in collection");
        }

        var entity = collectionItemMapper.toEntityFromWishListRequest(request);
        entity.setCollection(collection);
        entity.setAddedAt(LocalDateTime.now());
        entity.setProductVariant(productVariant);
        var saved = collectionItemRepository.save(entity);
        var response = collectionItemMapper.toFromWishListResponse(saved);

        auditLogService.createAuditLog(collection.getUser(), "Add Product to Collection", "Product added to collection successfully. Product id: " + saved.getId());

        log.info("Actionlog.addProductToCollectionFromWishList.end : wishListId={}", request.getWishListId());
        return ApiResponse.<CollectionItemFromWishListResponse>builder()
                .status(200)
                .message("Product added to collection from wishlist successfully")
                .data(response)
                .build();

//        log.info("Actionlog.addProductToCollection.start : productVariantId={}", request.getProductVariantId());
//        var collectionItem = collectionItemRepository.findByProductVariant_IdAndCollection_Id(request.getProductVariantId(), request.getCollectionId()).orElse(null);
//
//        if (collectionItem != null) {
//            throw new RuntimeException("Product already exists in collection");
//        }
//        var collection = collectionRepository.findById(request.getCollectionId()).orElseThrow(() -> new RuntimeException("Collection not found with id: " + request.getCollectionId()));
//        var productFromwishList = wishlistRepository.findByProductVariant_Id(request.getProductVariantId()).orElseThrow(
//                () -> new RuntimeException("ProductVariant not found in wishlist with id: " + request.getProductVariantId())
//        );
//
//        var entity = collectionItemMapper.toEntity(request);
//        entity.setCollection(collection);
//        entity.setAddedAt(LocalDateTime.now());
//        entity.setProductVariant(productFromwishList.getProductVariant());
//        var saved = collectionItemRepository.save(entity);
//        var response = collectionItemMapper.toResponse(saved);
//        log.info("Actionlog.addProductToCollection.end : productVariantId={}", request.getProductVariantId());
//        return response;

    }

    @Override
    public ApiResponse<List<CollectionResponse>> getAllCollections() {
        log.info("Actionlog.getAllCollections.start : ");
        Long userId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("userId");
        var collections = collectionRepository.findByUser_Id(userId);
        var response = collectionMapper.toResponseList(collections);
        log.info("Actionlog.getAllCollections.end : ");
        return ApiResponse.<List<CollectionResponse>>builder()
                .status(200)
                .message("Collections retrieved successfully")
                .data(response)
                .build();
    }


    @Override
    public ApiResponse<Void> shareCollection(Long collectionId, Long targetUserId) {
        log.info("Actionlog.shareCollection.start : collectionId={}", collectionId);

        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found"));
        var ownerUserId = user.getId();
        var collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new NotFoundException("Collection not found"));

        if (!collection.getUser().getId().equals(ownerUserId)) {
            throw new RuntimeException("You can only share your own collection");
        }

        var targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("Target user not found"));

        collection.getSharedWith().add(targetUser);
        collectionRepository.save(collection);
        auditLogService.createAuditLog(user, "Share Collection", "Collection shared successfully. Collection id: " + collection.getId());
        notificationService.sendNotification(targetUser, "New collection shared with you", NotificationType.COLLECTION_SHARED, collectionId);
        log.info("Actionlog.shareCollection.end : collectionId={}", collectionId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Collection shared successfully")
                .data(null)
                .build();
    }


    @Override
    public ApiResponse<CollectionResponse> readSharedCollection(Long collectionId, Long userId) {
        log.info("Actionlog.readSharedCollection.start : collectionId={}", collectionId);
        var collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
        var owner = collection.getUser();
        boolean isOwner = collection.getUser().getId().equals(userId);
        boolean isSharedWithUser = collection.getSharedWith().stream()
                .anyMatch(u -> u.getId().equals(userId));

        if (!isOwner && !isSharedWithUser) {
            throw new RuntimeException("You dont have access to this collection");
        }

        var collectionItems = collectionItemRepository.findByCollection_Id(collection.getId());


        var productVariantIds = collectionItems.stream()
                .map(item -> item.getProductVariant().getId())
                .toList();

        var itemResponses = collectionItems.stream()
                .map(item -> new CollectionItemResponse(
                        item.getId(),
                        item.getProductVariant().getId(),
                        item.getProductVariant().getProduct().getName(),
                        item.getAddedAt()
                ))
                .toList();

        var viewCount = collection.getViewCount() + 1;
        var response = CollectionResponse.builder()
                .id(collection.getId())
                .userId(collection.getUser().getId())
                .name(collection.getName())
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .isShared(!collection.getSharedWith().isEmpty())
                .shareToken(collection.getShareToken())
                .productVariantIds(productVariantIds)
                .items(itemResponses)
                .viewCount(viewCount)
                .build();

        collection.setViewCount(viewCount);
        collectionRepository.save(collection);

        auditLogService.createAuditLog(collection.getUser(), "Read Shared Collection", "Collection read successfully. Collection id: " + collection.getId());
        notificationService.sendNotification(owner, "Shared collection read", NotificationType.COLLECTION_READ, collectionId);
        log.info("Actionlog.readSharedCollection.end : collectionId={}", collectionId);
        return ApiResponse.<CollectionResponse>builder()
                .status(200)
                .message("Shared collection read successfully")
                .data(response)
                .build();
    }


    @Override
    public ApiResponse<CollectionResponse> renameCollection(Long collectionId, String newName) {
        log.info("Actionlog.renameCollection.start : collectionId={}", collectionId);
        var collection = collectionRepository.findById(collectionId).orElseThrow(() -> new NotFoundException("Collection not found"));
        var user = collection.getUser();
        if (!user.getId().equals(getCurrentUserId())) {
            throw new RuntimeException("You don't have permission to rename this collection");
        }
        collection.setName(newName);
        collectionRepository.save(collection);
        var response = collectionMapper.toResponse(collection);
        auditLogService.createAuditLog(user, "Rename Collection", "Collection renamed successfully. Collection id: " + collection.getId());
        log.info("Actionlog.renameCollection.end : collectionId={}", collectionId);
        return ApiResponse.<CollectionResponse>builder()
                .status(200)
                .message("Collection renamed successfully")
                .data(response)
                .build();
    }

    @Override
    public Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}
