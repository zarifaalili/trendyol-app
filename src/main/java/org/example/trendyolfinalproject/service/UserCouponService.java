package org.example.trendyolfinalproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.BasketElement;
import org.example.trendyolfinalproject.dao.entity.UserCoupon;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.CouponUsageLimitExceededException;
import org.example.trendyolfinalproject.exception.customExceptions.MinimumOrderAmountNotMetException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.model.DiscountType;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCouponService {
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final BasketRepository basketRepository;
    private final BasketElementRepository basketElementRepository;
    private final UserRepository userRepository;
    private final BasketService basketService;
    private final AuditLogService auditLogService;
    private final OrderRepository orderRepository;


    @Transactional
    public ApiResponse<String> useUserCoupon(Long couponId) {
        var userId = getCurrentUserId();
        log.info("Actionlog.useUserCoupon.start : userId={}, couponId={}", userId, couponId);

        var coupon = couponRepository.findById(couponId).orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));
        var basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Basket not found"));


        if (Boolean.TRUE.equals(coupon.getFirstOrderOnly())) {
            long orderCount = orderRepository.countByUser_Id(userId);
            if (orderCount > 0) {
                throw new RuntimeException("This coupon can only be used on your first order");
            }
        }

        var minOrderCount = coupon.getMinOrderCount();
        var userOrderCount = orderRepository.countByUser_Id(userId);
        if (minOrderCount != null
                && minOrderCount != userOrderCount) {
            throw new RuntimeException("You cant use this coupon");
        }

        if (basket.getDiscountAmount() != null && basket.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("A coupon has already been applied to this basket. Please cancel it before applying a new one.");
        }
        if (!coupon.getIsActive() || coupon.getEndDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Coupon is not active or expired");
        }


        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var check = userCouponRepository.findByUserAndCoupon(user, coupon);
        UserCoupon entity;
        if (check.isPresent()) {
            entity = check.get();
            if (coupon.getPerUserLimit() != null && entity.getUsageCount().intValue() >= coupon.getPerUserLimit()) {
                throw new CouponUsageLimitExceededException("Coupon usage limit reached");
//            throw new RuntimeException("Coupon is already used");
            }
//            var basket = basketRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Basket not found"));
//            List<BasketElement> basketElements = basketElementRepository.findByBasket_Id(basket.getId());

        } else {
            entity = new UserCoupon();
            entity.setUser(user);
            entity.setCoupon(coupon);
            entity.setUsageCount(BigDecimal.ZERO);
        }


        List<BasketElement> basketElements = basketElementRepository.findByBasket_Id(basket.getId());

        if (basketElements.isEmpty()) {
            throw new RuntimeException("Basket is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BasketElement basketElement : basketElements) {
            if (basketElement.getProductId() == null || basketElement.getProductId().getPrice() == null) {
                log.warn("BasketElement with id {} has a null product or product price. Skipping.", basketElement.getId());
                continue;
            }
            totalAmount = totalAmount.add(basketElement.getProductId().getPrice().multiply(BigDecimal.valueOf(basketElement.getQuantity())));

        }

//                    .stream()
//                    .map(basketElement -> basketElement.getProductId().getPrice()
//                            .multiply(BigDecimal.valueOf(basketElement.getQuantity())))
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//            var minAdd = coupon.getMinimumOrderAmount().subtract(totalAmount);

        if (coupon.getMinimumOrderAmount() != null && totalAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            BigDecimal minAdd = coupon.getMinimumOrderAmount().subtract(totalAmount);
            throw new MinimumOrderAmountNotMetException("Minimum order amount not met. You should add at least this much. " + minAdd
                    + "more to use this coupon. " +
                    "Minimum required amount is " + coupon.getMinimumOrderAmount());
        }


        BigDecimal discountAmount;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = totalAmount.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
            if (coupon.getMaximumDiscountAmount() != null && discountAmount.compareTo(coupon.getMaximumDiscountAmount()) > 0) {
                discountAmount = coupon.getMaximumDiscountAmount();
            }

        } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discountAmount = coupon.getDiscountValue();
            if (discountAmount.compareTo(totalAmount) > 0) {
                discountAmount = totalAmount;
            }
        } else {
            throw new RuntimeException("Invalid discount type");
        }

        basket.setDiscountAmount(discountAmount);
        basket.setFinalAmount(totalAmount.subtract(discountAmount));
        basketRepository.save(basket);

        entity.setUsageCount(entity.getUsageCount().add(BigDecimal.ONE));
        entity.setLastUseddate(LocalDateTime.now());
        userCouponRepository.save(entity);

        if (coupon.getUsageLimit() != null && coupon.getUsageLimit() > 0) {
            coupon.setUsageLimit(coupon.getUsageLimit() - 1);
            if (coupon.getUsageLimit() == 0) {
                coupon.setIsActive(false);
            }
            couponRepository.save(coupon);
        }


        auditLogService.createAuditLog(user, "Use Coupon", "Used coupon with id " + couponId);
        log.info("Actionlog.useUserCoupon.end : userId={}, couponId={}", userId, couponId);

        return ApiResponse.success("Coupon applied successfully");

    }


    public ApiResponse<String> cancelUserCoupon(Long couponId) {
        var userId = getCurrentUserId();

        log.info("Actionlog.cancelUserCoupon.start : userId={}, couponId={}", userId, couponId);
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        var coupon = couponRepository.findById(couponId).orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));

        var userCouponUsed = userCouponRepository.findByUserAndCoupon(user, coupon);
        if (userCouponUsed.isEmpty()) {
            throw new RuntimeException("User hasnt used yhis coupon");
        }

        var userCoupon = userCouponUsed.get();
        if (userCoupon.getUsageCount().intValue() == 0) {
            throw new RuntimeException("user has no usage of this coupon to cancel");
        }

        userCoupon.setUsageCount(userCoupon.getUsageCount().subtract(BigDecimal.ONE));
        userCoupon.setLastUseddate(null);
        userCouponRepository.save(userCoupon);

        if (coupon.getUsageLimit() != null && coupon.getUsageLimit() > 0) {
            coupon.setUsageLimit(coupon.getUsageLimit() + 1);
            couponRepository.save(coupon);
        }

        var basket1 = basketRepository.findByUserId(userId);
        BigDecimal rawTotal = basketService.calculateRawTotalAmount().getData();
        if (basket1.isPresent()) {
            var basket = basket1.get();
            basket.setDiscountAmount(BigDecimal.ZERO);
            basket.setFinalAmount(rawTotal);
            basketRepository.save(basket);

        }

        auditLogService.createAuditLog(user, "CANCEL COUPON", "Coupon cancelled");
        log.info("Actionlog.cancelUserCoupon.end : userId={}, couponId={}", userId, couponId);

        return ApiResponse.success("Coupon cancelled successfully");

    }


    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}