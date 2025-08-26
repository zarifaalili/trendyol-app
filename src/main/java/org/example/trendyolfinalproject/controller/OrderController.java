package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.OrderCreateRequest;
import org.example.trendyolfinalproject.response.OrderResponse;
import org.example.trendyolfinalproject.response.ReturnRequestResponse;
import org.example.trendyolfinalproject.response.SellerRevenueResponse;
import org.example.trendyolfinalproject.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    OrderResponse createOrder(@RequestBody @Valid OrderCreateRequest request) {
        return orderService.createOrder(request);
    }

    @DeleteMapping("/cancelOrder/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    void cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
    }


    @GetMapping("/getOrders")
    @PreAuthorize("hasRole('CUSTOMER')")
    List<OrderResponse> getOrders() {
        return orderService.getOrders();
    }


    @GetMapping("/getContinuedOrders")
    @PreAuthorize("hasRole('CUSTOMER')")
    List<OrderResponse> getContinuedOrders() {
        return orderService.getContinuedOrders();
    }

    @GetMapping("/getCancelledOrders")
    @PreAuthorize("hasRole('CUSTOMER')")
    List<OrderResponse> getCancelledOrdersByUserId() {
        return orderService.getCancelledOrders();
    }

    @GetMapping("/searchProductInOrders/{productName}")
    @PreAuthorize("hasRole('CUSTOMER')")
    List<OrderResponse> searchProductInOrders(@PathVariable String productName) {
        return orderService.searchProductInOrders(productName);
    }


    @GetMapping("/getOrdersBySellerId/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    List<OrderResponse> getOrdersBySellerId(@PathVariable Long sellerId) {
        return orderService.getOrdersBySeller(sellerId);
    }

    @GetMapping("/{sellerId}/revenue-stats")
    @PreAuthorize("hasRole('ADMIN')")
    SellerRevenueResponse getSellerRevenueStats(@PathVariable Long sellerId) {
        return orderService.getSellerRevenueStats(sellerId);
    }

    @PostMapping(value = "/send-refund-request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createRefundRequest(
            @RequestParam("orderItemId") Long orderItemId,
            @RequestParam("reason") String reason,
            @RequestPart("imageFile") MultipartFile imageFile) {

        orderService.sendReturnRequest(orderItemId, reason, imageFile);

        return ResponseEntity.ok("Refund request created successfully!");
    }

    @GetMapping("/refund-request/status/{requestId}")
    public String getRefundRequestStatus(@PathVariable Long requestId) {
        return orderService.getReturnRequestStatus(requestId);
    }

    @GetMapping("/refund-request/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReturnRequestResponse> getAllReturnRequests() {
        return orderService.getReturnRequests();
    }

    @PatchMapping("/refund-request/admin/accept/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String acceptReturnRequest(@PathVariable Long requestId) {
        return orderService.approveReturnRequest(requestId);
    }


    @GetMapping("/refund-request/admin/approved")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReturnRequestResponse> getApprovedReturnRequests() {
        return orderService.getApprovedReturnRequests();
    }

    @GetMapping("/refund-request/admin/not-approved")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReturnRequestResponse> getNotApprovedReturnRequests() {
        return orderService.getNotApprovedReturnRequests();
    }


    @GetMapping("/refund-request/user/all")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ReturnRequestResponse> getApprovedReturnRequestsByUser() {
        return orderService.getReturnRequestsByUser();
    }
}
