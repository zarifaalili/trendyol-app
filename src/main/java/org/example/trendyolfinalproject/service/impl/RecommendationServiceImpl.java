package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.ProductVariantMapper;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.RecommendationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    public ApiResponse<Void> saveUserView(Long userId, Long productVariantId) {
        log.info("Saving user view");
        String key = "user:views:" + userId;
        long timestamp = System.currentTimeMillis();

        redisTemplate.opsForZSet().remove(key, productVariantId.toString());
        redisTemplate.opsForZSet().add(key, productVariantId.toString(), timestamp);
        redisTemplate.opsForZSet().removeRange(key, 0, -11);
        log.info("User view saved successfully");
        return ApiResponse.<Void>builder()
                .status(200)
                .message("User view saved successfully")
                .data(null)
                .build();
    }

    @Override
    public ApiResponse<List<Long>> getLastViewedIds(Long userId) {
        log.info("Fetching last viewed products");
        String key = "user:views:" + userId;
        var ids = redisTemplate.opsForZSet().reverseRange(key, 0, 9);
        if (ids == null || ids.isEmpty()) {
            return ApiResponse.<List<Long>>builder()
                    .status(200)
                    .message("No viewed products found")
                    .data(List.of())
                    .build();
        }
        var idList = ids.stream().map(x -> Long.parseLong(x.toString())).toList();
        log.info("Last viewed products fetched successfully");
        return ApiResponse.<List<Long>>builder()
                .status(200)
                .message("Last viewed products fetched successfully")
                .data(idList)
                .build();
    }

    @Override
    public ApiResponse<List<ProductVariantResponse>> getSimilarProduct(Long productId) {
        log.info("Actionlog.getSimilarProduct.start : product={}", productId);
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var product = productRepository.findByIdAndStatus(productId, Status.ACTIVE).orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
        if (product == null) {
            throw new NotFoundException("Product not found with id: " + productId);
        }
        BigDecimal minPrice = product.getPrice().multiply(new BigDecimal("0.8"));
        BigDecimal maxPrice = product.getPrice().multiply(new BigDecimal("1.2"));

        var similarVariants = productVariantRepository.findSimilarProductVariants(
                product.getCategory().getId(), minPrice, maxPrice, product.getId()
        );
        if (similarVariants.isEmpty()) {
            throw new NotFoundException("No similar products found");
        }
        var mapper = productVariantMapper.toResponseList(similarVariants);
        auditLogService.createAuditLog(user, "Get similar product", "Get similar product successfully. Product id: " + product.getId());
        log.info("Actionlog.getSimilarProduct.end : product={}", productId);
        return ApiResponse.<List<ProductVariantResponse>>builder()
                .status(200)
                .message("Similar products fetched successfully")
                .data(mapper)
                .build();
    }

    @Override
    public ApiResponse<List<ProductVariantResponse>> getTrendingProducts() {
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        List<Object[]> trendingObjects = orderItemRepository.findTrendingProductsWithSales(LocalDateTime.now().minusDays(7));

        if (trendingObjects.isEmpty()) {
            trendingObjects = orderItemRepository.findDefaultTrendingProductsWithSales();
        }
        List<ProductVariantResponse> trending = trendingObjects.stream()
                .map(obj -> {
                    ProductVariant variant = (ProductVariant) obj[0];
                    return productVariantMapper.toResponse(variant);
                })
                .toList();

        auditLogService.createAuditLog(user, "Get trending product", "Get trending product successfully.");
        return ApiResponse.<List<ProductVariantResponse>>builder()
                .status(200)
                .message("Trending products fetched successfully")
                .data(trending)
                .build();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}

