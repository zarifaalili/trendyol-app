package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.OrderCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.OrderResponse;
import org.example.trendyolfinalproject.model.response.SellerRevenueResponse;
import org.example.trendyolfinalproject.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @DeleteMapping("/cancel/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(orderService.cancelOrder(orderId));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders() {
        return ResponseEntity.ok().body(orderService.getOrders());
    }

    @GetMapping("/continued")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getContinuedOrders() {
        return ResponseEntity.ok().body(orderService.getContinuedOrders());
    }

    @GetMapping("/cancelled")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getCancelledOrdersByUserId() {
        return ResponseEntity.ok().body(orderService.getCancelledOrders());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> searchProductInOrders(@RequestParam("productName") String productName) {
        return ResponseEntity.ok().body(orderService.searchProductInOrders(productName));
    }

    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersBySellerId(@PathVariable Long sellerId) {
        return ResponseEntity.ok().body(orderService.getOrdersBySeller(sellerId));
    }

    @GetMapping("/seller/{sellerId}/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SellerRevenueResponse>> getSellerRevenueStats(@PathVariable Long sellerId) {
        return ResponseEntity.ok().body(orderService.getSellerRevenueStats(sellerId));
    }


}
