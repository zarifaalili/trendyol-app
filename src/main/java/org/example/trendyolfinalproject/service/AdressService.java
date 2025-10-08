package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.AdressCreateRequest;
import org.example.trendyolfinalproject.model.response.AdressResponse;
import org.example.trendyolfinalproject.model.response.ApiResponse;

import java.util.List;

public interface AdressService {

    ApiResponse<AdressResponse> createAdress(AdressCreateRequest request);

    ApiResponse<String> deleteAdress(Long id);

    ApiResponse<List<AdressResponse>> getAdresses();

    ApiResponse<AdressResponse> updateAdress(Long id, AdressCreateRequest request);


}
