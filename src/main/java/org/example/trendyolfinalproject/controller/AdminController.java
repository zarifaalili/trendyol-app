package org.example.trendyolfinalproject.controller;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.example.trendyolfinalproject.response.AuditLogResponse;
import org.example.trendyolfinalproject.response.SalesReportResponse;
import org.example.trendyolfinalproject.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/approveSeller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveSeller(@PathVariable Long sellerId) {

        return adminService.approveSeller(sellerId);
    }

    @PostMapping("/rejectSeller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectSeller(@PathVariable Long sellerId) {

        return adminService.rejectSeller(sellerId);
    }

    @GetMapping("/getAdmins")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> getAdmins() {

        return adminService.getAllAdmins();
    }

    @PostMapping("/paySellersForToday")
    @PreAuthorize("hasRole('ADMIN')")
    public void paySellersForToday() {
        adminService.paySellersForToday();
    }


    @GetMapping("/getSalesReport")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SalesReportResponse> getSalesReport(@PathParam("startDate") LocalDateTime startDate) {
        return adminService.getSalesReport(startDate);
    }


    @GetMapping("/getUserActivity/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<AuditLogResponse>> getUserActivity(@PathVariable Long userId) {
        return adminService.getUserAuditLogs(userId);
    }


}
