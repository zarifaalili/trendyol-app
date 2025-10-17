package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.AuditLogResponse;
import org.example.trendyolfinalproject.model.response.SalesReportResponse;
import org.example.trendyolfinalproject.model.response.UserResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    ApiResponse<String> approveSeller(Long sellerId);

    ApiResponse<String> rejectSeller(Long sellerId);

    ApiResponse<List<UserResponse>> getAllAdmins();

    void paySellersForToday();

    ApiResponse<SalesReportResponse> getSalesReport(LocalDateTime since);

    ApiResponse<List<AuditLogResponse>> getUserAuditLogs(Long userId);


}
