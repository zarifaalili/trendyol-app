package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ReturnRequestResponse;
import org.example.trendyolfinalproject.service.ReturnRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/return-requests")
@RequiredArgsConstructor
public class ReturnRequestController {

    private final ReturnRequestService orderService;

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> createRefundRequest(
            @RequestParam("orderItemId") Long orderItemId,
            @RequestParam("reason") String reason,
            @RequestPart("imageFile") MultipartFile imageFile) {

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.sendReturnRequest(orderItemId, reason, imageFile));

    }

    @GetMapping("/{requestId}/status")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> getRefundRequestStatus(@PathVariable Long requestId) {
        return ResponseEntity.ok().body(orderService.getReturnRequestStatus(requestId));
    }

    @GetMapping("/return-request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReturnRequestResponse>>> getAllReturnRequests() {
        return ResponseEntity.ok().body(orderService.getReturnRequests());
    }

    @PatchMapping("/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> acceptReturnRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok().body(orderService.approveReturnRequest(requestId));
    }


    @GetMapping("/approved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReturnRequestResponse>>> getApprovedReturnRequests() {
        return ResponseEntity.ok().body(orderService.getApprovedReturnRequests());
    }

    @GetMapping("/not-approved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReturnRequestResponse>>> getNotApprovedReturnRequests() {
        return ResponseEntity.ok().body(orderService.getNotApprovedReturnRequests());
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<ReturnRequestResponse>>> getApprovedReturnRequestsByUser() {
        return ResponseEntity.ok().body(orderService.getReturnRequestsByUser());
    }
}
