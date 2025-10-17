package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.UserCouponResponse;
import org.example.trendyolfinalproject.service.UserCouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user-coupons")
@RequiredArgsConstructor
public class UserCouponController {
    private final UserCouponService userCouponService;

    @PostMapping("/{couponId}/use")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> useUserCoupon(@PathVariable Long couponId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userCouponService.useUserCoupon(couponId));
    }

    @DeleteMapping("/{couponId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> cancelUserCoupon(@PathVariable Long couponId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userCouponService.cancelUserCoupon(couponId));
    }

    @GetMapping("/history/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserCouponResponse>>> getUserCouponHistory(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userCouponService.getUserCouponHistory(userId));
    }
}
