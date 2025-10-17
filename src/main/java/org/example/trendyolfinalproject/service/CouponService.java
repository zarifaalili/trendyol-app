package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.CouponCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CouponResponse;

import java.util.List;

public interface CouponService {

    ApiResponse<CouponResponse> createCoupon(CouponCreateRequest request);

    ApiResponse<Void> deleteCoupon(Long id);

    ApiResponse<String> deactiveCoupon(Long id);

    ApiResponse<String> activeCoupon(Long id);

    ApiResponse<List<CouponResponse>> getActiveCoupons();

    ApiResponse<List<CouponResponse>> getAllCoupons();

    ApiResponse<List<CouponResponse>> getExpiredCoupons();

    ApiResponse<String> checkValidCoupon(String code);


}
