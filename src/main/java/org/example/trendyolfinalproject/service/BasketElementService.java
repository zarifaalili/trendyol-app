package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.BasketElementRequest;
import org.example.trendyolfinalproject.model.request.DeleteBasketElementRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.BasketElementResponse;

import java.util.List;

public interface BasketElementService {

    ApiResponse<BasketElementResponse> createBasketElement(BasketElementRequest request);

    ApiResponse<Void> deleteBasketElement(DeleteBasketElementRequest request);

    ApiResponse<BasketElementResponse> decrieceQuantity(DeleteBasketElementRequest request);

    ApiResponse<List<BasketElementResponse>> getBasketElements();
}
