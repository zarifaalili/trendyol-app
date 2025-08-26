package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.service.UserCouponService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/userCoupon")
@RequiredArgsConstructor
public class UserCouponController {
    private final UserCouponService userCouponService;

    @PostMapping("/useUserCoupon/{couponId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public void useUserCoupon(@PathVariable Long couponId) {
        userCouponService.useUserCoupon(couponId);
    }

    @PostMapping("/cancelUserCoupon/{couponId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public void cancelUserCoupon( @PathVariable Long couponId) {
        userCouponService.cancelUserCoupon(couponId);
    }

}
