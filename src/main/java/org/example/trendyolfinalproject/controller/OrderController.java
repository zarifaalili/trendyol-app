package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.OrderCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.OrderResponse;
import org.example.trendyolfinalproject.model.response.ReturnRequestResponse;
import org.example.trendyolfinalproject.model.response.SellerRevenueResponse;
import org.example.trendyolfinalproject.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<OrderResponse> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        return orderService.createOrder(request);
    }

    @DeleteMapping("/cancel/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<String> cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }


    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<List<OrderResponse>> getOrders() {
        return orderService.getOrders();
    }


    @GetMapping("/continued")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<List<OrderResponse>> getContinuedOrders() {
        return orderService.getContinuedOrders();
    }

    @GetMapping("/cancelled")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<OrderResponse>> getCancelledOrdersByUserId() {
        return orderService.getCancelledOrders();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<List<OrderResponse>> searchProductInOrders(@RequestParam("productName") String productName) {
        return orderService.searchProductInOrders(productName);
    }

    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<OrderResponse>> getOrdersBySellerId(@PathVariable Long sellerId) {
        return orderService.getOrdersBySeller(sellerId);
    }

    @GetMapping("/seller/{sellerId}/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<SellerRevenueResponse> getSellerRevenueStats(@PathVariable Long sellerId) {
        return orderService.getSellerRevenueStats(sellerId);
    }


}
