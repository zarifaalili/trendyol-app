package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.UserCouponResponse;

import java.util.List;

public interface UserCouponService {

    ApiResponse<String> useUserCoupon(Long couponId);

    ApiResponse<Void> cancelUserCoupon(Long couponId);

    ApiResponse<List<UserCouponResponse>> getUserCouponHistory(Long userId);

}
