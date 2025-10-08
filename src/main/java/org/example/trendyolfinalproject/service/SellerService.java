package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.SellerCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SellerResponse;

import java.util.List;

public interface SellerService {

    ApiResponse<SellerResponse> createSeller(SellerCreateRequest request);

    ApiResponse<List<SellerResponse>> getSellers();

    ApiResponse<SellerResponse> getSeller(String companyName);

    ApiResponse<Double> getSellerAverageRating(Long sellerId);
}
