package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ReturnRequestResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReturnRequestService {

    ApiResponse<String> sendReturnRequest(Long orderItemId, String reason, MultipartFile imageFile);

    ApiResponse<String> getReturnRequestStatus(Long returnRequestId);

    ApiResponse<List<ReturnRequestResponse>> getReturnRequests();

    ApiResponse<List<ReturnRequestResponse>> getReturnRequestsByUser();

    ApiResponse<List<ReturnRequestResponse>> getNotApprovedReturnRequests();

    ApiResponse<List<ReturnRequestResponse>> getApprovedReturnRequests();

    ApiResponse<String> approveReturnRequest(Long returnRequestId);
}
