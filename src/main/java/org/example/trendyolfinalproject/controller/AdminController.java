package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.AuditLogResponse;
import org.example.trendyolfinalproject.model.response.SalesReportResponse;
import org.example.trendyolfinalproject.model.response.UserResponse;
import org.example.trendyolfinalproject.service.AdminService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/approve-seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> approveSeller(@PathVariable Long sellerId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.approveSeller(sellerId));
    }

    @PostMapping("/sellers/{sellerId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> rejectSeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok().body(adminService.rejectSeller(sellerId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAdmins() {
        return ResponseEntity.ok().body(adminService.getAllAdmins());
    }

    @GetMapping("/sales-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SalesReportResponse>> getSalesReport(@RequestParam("startDate")   LocalDateTime startDate) {
        return ResponseEntity.ok().body(adminService.getSalesReport(startDate));
    }

    @GetMapping("/users/{userId}/activity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getUserActivity(@PathVariable Long userId) {
        return ResponseEntity.ok().body(adminService.getUserAuditLogs(userId));
    }

}
