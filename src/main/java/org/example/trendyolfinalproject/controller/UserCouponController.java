package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.example.trendyolfinalproject.response.UserCouponResponse;
import org.example.trendyolfinalproject.service.UserCouponService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/userCoupon")
@RequiredArgsConstructor
public class UserCouponController {
    private final UserCouponService userCouponService;

    @PostMapping("/useUserCoupon/{couponId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> useUserCoupon(@PathVariable Long couponId) {

        return userCouponService.useUserCoupon(couponId);
    }

    @PostMapping("/cancelUserCoupon/{couponId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> cancelUserCoupon(@PathVariable Long couponId) {
        return userCouponService.cancelUserCoupon(couponId);
    }

    @GetMapping("/history/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserCouponResponse>> getUserCouponHistory(@PathVariable Long userId) {

        return userCouponService.getUserCouponHistory(userId);
    }
}
