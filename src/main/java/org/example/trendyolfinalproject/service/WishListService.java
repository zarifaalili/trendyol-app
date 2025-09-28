package org.example.trendyolfinalproject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.ProductVariantRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.dao.repository.WishlistRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.WishListMapper;
import org.example.trendyolfinalproject.model.enums.Role;
import org.example.trendyolfinalproject.model.request.WishListCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.WishListResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishListService {
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final WishListMapper wishListMapper;
    private final ProductVariantRepository productVariantRepository;
    private final AuditLogService auditLogService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    private static final String WISHLIST_KEY_PREFIX = "wishlist:";


    public ApiResponse<WishListResponse> addToFavorite(WishListCreateRequest request) {
        var userId = getCurrentUserId();

        if (userId == null) {
            throw new RuntimeException("You are not logged in");
        }
        log.info("Actionlog.createWishList.start : userId={}", userId);
        var user = userRepository.findByIdAndRole(userId, Role.CUSTOMER).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        var productVariant = productVariantRepository.findById(request.getProductVariantId()).orElseThrow(() -> new RuntimeException("ProductVariant not found with id: " + request.getProductVariantId()));
        var wishlist = wishlistRepository.findByUserAndProductVariant_Id((user), productVariant.getId()).orElse(null);

        if (wishlist != null) {
            throw new RuntimeException("ProductVariant already exists in wishlist with id: " + request.getProductVariantId());
        }
        var entity = wishListMapper.toEntity(request);
        entity.setUser(user);
        entity.setProductVariant(productVariant);
        var saved = wishlistRepository.save(entity);
        var response = wishListMapper.toResponse(saved);
        auditLogService.createAuditLog(user, "Add to Favorite", "Created favorite with id: " + saved.getId());
        log.info("Actionlog.createWishList.end : userId={}", user);

        return ApiResponse
                .<WishListResponse>builder()
                .status(200)
                .message("Created favorite with id: " + saved.getId())
                .data(response)
                .build();

    }

    public ApiResponse<String> deleteFromFavorites(Long id) {
        log.info("Actionlog.deleteFromFavorites.start : id={}", id);

        var userId = getCurrentUserId();
        var favorite = wishlistRepository.findById(id).orElseThrow(() -> new RuntimeException("Favorite not found with id: " + id));
        if (!favorite.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to delete this favorite");
        }
        wishlistRepository.deleteById(id);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId)), "DELETE Favorite", "Deleted favorite with id: " + id);
        log.info("Actionlog.deleteFromFavorites.end : id={}", id);

        return ApiResponse.<String>builder()
                .status(200)
                .message("Deleted favorite with id: " + id)
                .build();
    }

    public ApiResponse<List<WishListResponse>> getFavorites() {

        Long userId = getCurrentUserId();
        String cacheKey = WISHLIST_KEY_PREFIX + userId;

        var cached = redisTemplate.opsForValue().get(WISHLIST_KEY_PREFIX + getCurrentUserId());
        if (cached != null) {
            log.info("Actionlog.getFavorites.start : ");
            var obj = objectMapper.convertValue(cached, new TypeReference<List<WishListResponse>>() {
            });

            return ApiResponse.<List<WishListResponse>>builder()
                    .status(200)
                    .message("Get favorite successfully. User id: " + userId)
                    .data(obj)
                    .build();


        }
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var favorites = wishlistRepository.findByUser(user);
        var mapper = wishListMapper.toResponseList(favorites);

        redisTemplate.opsForValue().set(cacheKey, mapper, Duration.ofMinutes(10));
        log.info("Actionlog.getFavorites.end : ");
        auditLogService.createAuditLog(user, "Get Favorites", "Get favorite successfully. User id: " + user.getId());
        return ApiResponse.<List<WishListResponse>>builder()
                .status(200)
                .message("Get favorite successfully. User id: " + userId)
                .data(mapper)
                .build();
    }


    public ApiResponse<List<WishListResponse>> serchWishList(String productName) {
        log.info("Actionlog.serchWishList.start : ");

        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var favorites = wishlistRepository.findByProductVariant_Product_NameContainingIgnoreCase(productName);
        if (favorites.isEmpty()) {
            throw new NotFoundException("Search result not found with product name in favorites : " + productName);
        }
        var mapper = wishListMapper.toResponseList(favorites);
        auditLogService.createAuditLog(user, "Serch WishList", "Serch favorite successfully. User id: " + user.getId());
        log.info("Actionlog.serchWishList.end : ");

        return ApiResponse.<List<WishListResponse>>builder()
                .status(200)
                .message("Serch favorite successfully. User id: " + user.getId())
                .data(mapper)
                .build();
    }


    public ApiResponse<List<WishListResponse>> getProductVariantsByDecreasedCost() {
        log.info("Actionlog.getProductVariantsByDecreasedCost.start : ");
        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        var favorites = wishlistRepository.getProductVariantsByDecreasedCost();
        if (favorites.isEmpty()) {
            throw new NotFoundException("Search result not found with decreased cost in favorites: ");
        }
        var mapper = wishListMapper.toResponseList(favorites);
        auditLogService.createAuditLog(user, "Get Product Variants By Decreased Cost", "Get favorite successfully. User id: " + user.getId());
        log.info("Actionlog.getProductVariantsByDecreasedCost.end: ");
        return ApiResponse.<List<WishListResponse>>builder()
                .status(200)
                .message("get product decriesed cost successfully. User id: " + user.getId())
                .data(mapper)
                .build();
    }


    public ApiResponse<Void> shareWishListWithUser(Long wishListId, Long userId) {
        log.info("Actionlog.shareWishListWithUser.start : ");

        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));

        var targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var wishList = wishlistRepository.findById(wishListId)
                .orElseThrow(() -> new NotFoundException("WishList not found"));


        if (!wishList.getUser().equals(user)) {
            throw new NotFoundException("Access denied: You cannot share this wishlist");
        }

        if (!wishList.getSharedWith().contains(targetUser)) {
            wishList.getSharedWith().add(targetUser);
            wishList.setIsShared(true);
            wishlistRepository.save(wishList);

        }

        return ApiResponse.<Void>builder()
                .status(200)
                .message("WishList shared successfully. User id: " + user.getId())
                .build();
    }


    public Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


}
