package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.CouponCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CouponResponse;
import org.example.trendyolfinalproject.service.CouponService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request) {
        return couponService.createCoupon(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCoupon(@PathVariable Long id) {
        return couponService.deleteCoupon(id);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deactiveCoupon(@PathVariable Long id) {
        return couponService.deactiveCoupon(id);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> activeCoupon(@PathVariable Long id) {
        return couponService.activeCoupon(id);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CouponResponse>>  getActiveCoupons() {
        return couponService.getActiveCoupons();
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CouponResponse>>  getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CouponResponse>>  getExpiredCoupons() {
        return couponService.getExpiredCoupons();
    }

    @GetMapping("/ckeck/{code}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ApiResponse<String> checkValidCoupon(@PathVariable String code) {
        return couponService.checkValidCoupon(code);
    }

}
