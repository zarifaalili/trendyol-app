package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.BrandCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.BrandResponse;

public interface BrandService {

    ApiResponse<BrandResponse> createBrand(BrandCreateRequest request);

    ApiResponse<BrandResponse> updateBrand(Long id, BrandCreateRequest request);

    ApiResponse<BrandResponse> getBrandbyName(String name);

}
