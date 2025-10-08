package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SellerFollowResponse;

import java.util.List;

public interface SellerFollowService {

    ApiResponse<String> follow(Long sellerId);

    ApiResponse<List<SellerFollowResponse>> getAllFollowers();

    ApiResponse<String> unfollow(Long sellerId);


}
