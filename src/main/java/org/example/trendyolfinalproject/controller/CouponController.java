package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.example.trendyolfinalproject.request.CouponCreateRequest;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.example.trendyolfinalproject.response.CouponResponse;
import org.example.trendyolfinalproject.service.CouponService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;


    @PostMapping("/createCoupon")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request) {
        return couponService.createCoupon(request);
    }

    @DeleteMapping("/deleteCoupon/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCoupon(@PathVariable Long id) {
        return couponService.deleteCoupon(id);
    }

    @PatchMapping("/deactiveCoupon/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deactiveCoupon(@PathVariable Long id) {
        return couponService.deactiveCoupon(id);
    }

    @PatchMapping("/activeCoupon/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> activeCoupon(@PathVariable Long id) {
        return couponService.activeCoupon(id);
    }

    @GetMapping("/getActiveCoupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CouponResponse>>  getActiveCoupons() {
        return couponService.getActiveCoupons();
    }


    @GetMapping("/getAllCoupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CouponResponse>>  getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/getExpiredCoupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CouponResponse>>  getExpiredCoupons() {
        return couponService.getExpiredCoupons();
    }

    @GetMapping("/checkValidCoupon/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> checkValidCoupon(@PathVariable String code) {
        return couponService.checkValidCoupon(code);
    }

}
