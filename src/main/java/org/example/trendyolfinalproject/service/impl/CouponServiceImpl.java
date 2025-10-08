package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.repository.CouponRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.CouponMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.request.CouponCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CouponResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.CouponService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    public ApiResponse<CouponResponse> createCoupon(CouponCreateRequest request) {

        log.info("Actionlog.createCoupon.start : ");

        Long userId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("userId");
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        var exist = couponRepository.findByCode(request.getCode()).orElse(null);
        if (exist != null) {
            throw new AlreadyException("Coupon code already exists");
        }
        var entity = couponMapper.toEntity(request);
        var saved = couponRepository.save(entity);
        var response = couponMapper.toResponse(saved);

        if (Boolean.TRUE.equals(request.getFirstOrderOnly())) {

            var newUsers = userRepository.findAllWithoutOrders();
            for (var newUser : newUsers) {
                notificationService.sendNotification(
                        newUser,
                        "Special coupon for your first order: " + saved.getCode(),
                        NotificationType.PROMOTION,
                        saved.getId()
                );
            }

        } else if (request.getMinOrderCount() != null) {

            var users = userRepository.findAllByOrderCount(request.getMinOrderCount().longValue());
            for (var u : users) {
                notificationService.sendNotification(
                        u,
                        "Special coupon for users with " + request.getMinOrderCount() + " orders: " + saved.getCode(),
                        NotificationType.PROMOTION,
                        saved.getId()
                );
            }

        } else {

            notificationService.sendToAllCustomers("There is a new coupon : " + saved.getCode(), NotificationType.PROMOTION, saved.getId());

        }
        auditLogService.createAuditLog(user, "Create Coupon", "Coupon created successfully. Coupon id: " + saved.getId());

        log.info("Actionlog.createCoupon.end : ");

        return new ApiResponse<>(HttpStatus.OK.value(), "Coupon created successfully", response);

    }

    @Override
    public ApiResponse<Void> deleteCoupon(Long id) {
        log.info("Actionlog.deleteCoupon.start : id={}", id);
        Long userId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("userId");
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        var coupon = couponRepository.findById(id).orElseThrow(() -> new RuntimeException("Coupon not found : " + id));
        couponRepository.deleteById(id);
        auditLogService.createAuditLog(user, "Delete Coupon", "Coupon deleted successfully. Coupon id: " + coupon.getCode());
        log.info("Actionlog.deleteCoupon.end : id={}", id);
        return new ApiResponse<>(HttpStatus.OK.value(), "Coupon deleted successfully", null);

    }


    @Override
    public  ApiResponse<String> deactiveCoupon(Long id) {
        log.info("Actionlog.deactiveCoupon.start : id={}", id);
        Long userId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("userId");
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        var coupon = couponRepository.findByIdAndIsActive(id, true).orElseThrow(() -> new RuntimeException("Active Coupon not found : " + id));
        coupon.setIsActive(false);
        couponRepository.save(coupon);
        auditLogService.createAuditLog(user, "Deactive Coupon", "Coupon deactive successfully. Coupon id: " + coupon.getCode());
        log.info("Actionlog.deactiveCoupon.end : id={}", id);
        return new ApiResponse<>(HttpStatus.OK.value(), "Coupon deactivated successfully", null);
    }

    @Override
    public ApiResponse<String> activeCoupon(Long id) {
        log.info("Actionlog.activeCoupon.start : id={}", id);
        Long userId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("userId");
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        var coupon = couponRepository.findByIdAndIsActive(id, false).orElseThrow(() -> new NotFoundException("Deactive Coupon not found : " + id));
        coupon.setIsActive(true);
        couponRepository.save(coupon);
        auditLogService.createAuditLog(user, "Active Coupon", "Coupon active successfully. Coupon id: " + coupon.getCode());
        log.info("Actionlog.activeCoupon.end : id={}", id);
        return new ApiResponse<>(HttpStatus.OK.value(), "Coupon activated successfully", null);
    }

    public ApiResponse<List<CouponResponse>> getActiveCoupons() {
        log.info("Actionlog.getActiveCoupons.start : ");
        var coupons = couponRepository.findByIsActive(true);
        var response = couponMapper.toResponseList(coupons);
        log.info("Actionlog.getActiveCoupons.end : ");
        return new ApiResponse<>(HttpStatus.OK.value(), "Active coupons fetched", response);
    }

    @Override
    public ApiResponse<List<CouponResponse>> getAllCoupons() {
        log.info("Actionlog.getAllCoupons.start : ");
        var coupons = couponRepository.findAll();
        var response = couponMapper.toResponseList(coupons);
        log.info("Actionlog.getAllCoupons.end : ");
        return new ApiResponse<>(HttpStatus.OK.value(), "All coupons fetched", response);
    }

    @Override
    public ApiResponse<List<CouponResponse>> getExpiredCoupons() {
        log.info("Actionlog.getExpiredCoupons.start : ");
        var coupons = couponRepository.findByEndDateBefore(LocalDateTime.now());
        var response = couponMapper.toResponseList(coupons);
        log.info("Actionlog.getExpiredCoupons.end : ");
        return new ApiResponse<>(HttpStatus.OK.value(), "Expired coupons fetched", response);
    }


    @Override
    public ApiResponse<String> checkValidCoupon(String code) {
        log.info("Actionlog.checkValidCoupon.start : code={}", code);
        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found"));
        var codee = code.toUpperCase();
        var coupon = couponRepository.findByCode(codee).orElseThrow(() -> new NotFoundException("Coupon not found"));

        String message;
        if (coupon.getIsActive()) {
            auditLogService.createAuditLog(user, "Check Valid Coupon", "Coupon is valid. Coupon id: " + coupon.getCode());
            message = "Coupon is valid";
        } else {
            auditLogService.createAuditLog(user, "Check Valid Coupon", "Coupon is not valid. Coupon id: " + coupon.getCode());
            message = "Coupon is not valid";
        }
        return new ApiResponse<>(HttpStatus.OK.value(), message, null);

    }


    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }


//    public void assignCoupon(Long userId, Long couponId) {
//        log.info("Actionlog.assignCoupon.start : userId={}, couponId={}", userId, couponId);
//        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
//        var coupon = couponRepository.findById(couponId).orElseThrow(() -> new NotFoundException("Coupon not found"));
//        if (coupon.getIsActive()) {
//            throw new AlreadyException("Coupon is already active");
//        }
//        coupon.setIsActive(true);
//        couponRepository.save(coupon);
//        auditLogService.createAuditLog(user, "Assign Coupon", "Coupon assigned successfully. Coupon id: " + coupon.getCode());
//        log.info("Actionlog.assignCoupon.end : userId={}, couponId={}", userId, couponId);
//    }

}
