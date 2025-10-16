package org.example.trendyolfinalproject.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Basket;
import org.example.trendyolfinalproject.dao.entity.BasketElement;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.BasketElementRepository;
import org.example.trendyolfinalproject.dao.repository.BasketRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.BasketMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.BasketElementResponse;
import org.example.trendyolfinalproject.model.response.BasketSummaryResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.BasketService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final BasketMapper basketMapper;
    private final BasketElementRepository basketElementRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;


    @Override
    public ApiResponse<BigDecimal> getTotalAmount() {
        Long currentUserId = getCurrentUserId();
        var basket1 = basketRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));

        log.info("Actionlog.getTotalAmount.start : basketId={}", basket1.getId());

        BigDecimal currentTotal = basket1.getFinalAmount();
        if (currentTotal == null) {
            currentTotal = calculateRawTotalAmount().getData();
        }

        var user = userRepository.findById(currentUserId).orElseThrow(() -> new NotFoundException("User not found with id: " + currentUserId));
        auditLogService.createAuditLog(user, "Get total amount of basket", "Get total price of basket successfully. Basket id: " + basket1.getId());

        log.info("Actionlog.getTotalAmount.end : basketId={}", basket1.getId());
        return ApiResponse.<BigDecimal>builder()
                .status(200)
                .message("Basket total retrieved successfully")
                .data(currentTotal)
                .build();
    }

    @Override
    public ApiResponse<BigDecimal> calculateRawTotalAmount() {
        Long currentUserId = getCurrentUserId();
        var basket1 = basketRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));
        log.info("Actionlog.getTotalPrice.start : basketId={}", basket1.getId(), "userId=" + currentUserId);

        var basketElements = basketElementRepository.findByBasket_Id(basket1.getId());
        BigDecimal rawTotal = BigDecimal.ZERO;
        for (BasketElement basketElement : basketElements) {
            if (basketElement.getProductId() == null || basketElement.getProductId().getPrice() == null) {
                log.warn("Product price is null. Product id: " + basketElement.getProductId().getId());
                continue;
            }
            BigDecimal proce = basketElement.getProductId().getPrice();
            BigDecimal subtotal = proce.multiply(BigDecimal.valueOf(basketElement.getQuantity()));
            rawTotal = rawTotal.add(subtotal);
        }
        log.info("Actionlog.getTotalPrice.end : basketId={}", basket1.getId());
        return ApiResponse.<BigDecimal>builder()
                .status(200)
                .message("Basket raw total retrieved successfully")
                .data(rawTotal)
                .build();
    }


    @Override
    public int notifyAbandonedBaskets() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<User> users = basketElementRepository.findAllUsersWithAbandonedBaskets(threshold);
        log.info("Found {} users with abandoned baskets", users.size());
        for (User user : users) {
            notificationService.sendNotification(
                    user,
                    "Your basket has been abandoned",
                    NotificationType.ABANDONED_BASKET,
                    null

            );
        }
        return users.size();
    }


    @Override
    public ApiResponse<BasketSummaryResponse> getBasketSummary() {
        log.info("Actionlog.getBasketSummary.start");
        Long currentUserId = getCurrentUserId();

        var basket = basketRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));

        var basketElements = basketElementRepository.findByBasket_Id(basket.getId());

        List<BasketElementResponse> elementResponses = basketElements.stream()
                .map(be -> {
                    BigDecimal price = be.getProductId().getPrice();
                    BigDecimal subtotal = price.multiply(BigDecimal.valueOf(be.getQuantity()));

                    return new BasketElementResponse(
                            be.getId(),
                            basket.getId(),
                            be.getProductId().getId(),
                            be.getProductId().getName(),
                            price,
                            be.getProductVariantId() != null ? be.getProductVariantId().getId() : null,
                            be.getProductVariantId() != null ? be.getProductVariantId().getProduct().getName() : null,
                            be.getQuantity(),
                            be.getAddedAt(),
                            subtotal
                    );
                })
                .toList();

        BigDecimal total = elementResponses.stream()
                .map(BasketElementResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = calculateDiscount(basket, elementResponses);
        BigDecimal finalAmount = total.subtract(discountAmount);

        var user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + currentUserId));
        log.debug("User found with id: {}", user.getId());
        auditLogService.createAuditLog(user, "Get basket summary",
                "Get basket summary successfully. Basket id: " + basket.getId());

        log.info("Actionlog.getBasketSummary.end");

        return ApiResponse.<BasketSummaryResponse>builder()
                .status(200)
                .message("Basket summary retrieved successfully")
                .data(BasketSummaryResponse.builder()
                        .basketElements(elementResponses)
                        .totalAmount(total)
                        .discountAmount(discountAmount)
                        .finalAmount(finalAmount)
                        .build())
                .build();
    }

    private BigDecimal calculateDiscount(Basket basket, List<BasketElementResponse> elements) {
        if (basket.getDiscountAmount() == null) {
            return BigDecimal.ZERO;
        }
        return basket.getDiscountAmount();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }


}
