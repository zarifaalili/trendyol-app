package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.OrderCreateRequest;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.example.trendyolfinalproject.response.OrderResponse;
import org.example.trendyolfinalproject.response.ReturnRequestResponse;
import org.example.trendyolfinalproject.response.SellerRevenueResponse;
import org.example.trendyolfinalproject.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/createOrder")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<OrderResponse> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        return orderService.createOrder(request);
    }

    @DeleteMapping("/cancelOrder/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<String> cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }


    @GetMapping("/getOrders")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<List<OrderResponse>> getOrders() {
        return orderService.getOrders();
    }


    @GetMapping("/getContinuedOrders")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<List<OrderResponse>> getContinuedOrders() {
        return orderService.getContinuedOrders();
    }

    @GetMapping("/getCancelledOrders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<OrderResponse>> getCancelledOrdersByUserId() {
        return orderService.getCancelledOrders();
    }

    @GetMapping("/searchProductInOrders/{productName}")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<List<OrderResponse>> searchProductInOrders(@PathVariable String productName) {
        return orderService.searchProductInOrders(productName);
    }


    @GetMapping("/getOrdersBySellerId/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<OrderResponse>> getOrdersBySellerId(@PathVariable Long sellerId) {
        return orderService.getOrdersBySeller(sellerId);
    }

    @GetMapping("/{sellerId}/revenue-stats")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<SellerRevenueResponse> getSellerRevenueStats(@PathVariable Long sellerId) {
        return orderService.getSellerRevenueStats(sellerId);
    }

    @PostMapping(value = "/send-refund-request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> createRefundRequest(
            @RequestParam("orderItemId") Long orderItemId,
            @RequestParam("reason") String reason,
            @RequestPart("imageFile") MultipartFile imageFile) {

        return orderService.sendReturnRequest(orderItemId, reason, imageFile);

    }

    @GetMapping("/refund-request/status/{requestId}")
    public ApiResponse<String> getRefundRequestStatus(@PathVariable Long requestId) {
        return orderService.getReturnRequestStatus(requestId);
    }

    @GetMapping("/refund-request/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReturnRequestResponse>> getAllReturnRequests() {
        return orderService.getReturnRequests();
    }

    @PatchMapping("/refund-request/admin/accept/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> acceptReturnRequest(@PathVariable Long requestId) {
        return orderService.approveReturnRequest(requestId);
    }


    @GetMapping("/refund-request/admin/approved")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReturnRequestResponse>> getApprovedReturnRequests() {
        return orderService.getApprovedReturnRequests();
    }

    @GetMapping("/refund-request/admin/not-approved")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReturnRequestResponse>> getNotApprovedReturnRequests() {
        return orderService.getNotApprovedReturnRequests();
    }


    @GetMapping("/refund-request/user/all")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<ReturnRequestResponse>> getApprovedReturnRequestsByUser() {
        return orderService.getReturnRequestsByUser();
    }
}
