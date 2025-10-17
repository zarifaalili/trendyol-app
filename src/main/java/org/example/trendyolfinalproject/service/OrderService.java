package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.model.request.OrderCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.OrderResponse;
import org.example.trendyolfinalproject.model.response.ReturnRequestResponse;
import org.example.trendyolfinalproject.model.response.SellerRevenueResponse;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderService {

    ApiResponse<OrderResponse> createOrder(OrderCreateRequest request);

    ApiResponse<Void> cancelOrder(Long orderId);

    ApiResponse<List<OrderResponse>> getOrders();

    ApiResponse<List<OrderResponse>> getContinuedOrders();

    ApiResponse<List<OrderResponse>> getCancelledOrders();

    ApiResponse<List<OrderResponse>> searchProductInOrders(String productName);

    Map<Seller, BigDecimal> calculateSellerEarningsToday();

    ApiResponse<List<OrderResponse>> getOrdersBySeller(Long sellerId);

    ApiResponse<SellerRevenueResponse> getSellerRevenueStats(Long sellerId);

}
