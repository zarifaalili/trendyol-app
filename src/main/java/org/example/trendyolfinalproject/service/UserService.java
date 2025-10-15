package org.example.trendyolfinalproject.service;

import jakarta.mail.MessagingException;
import org.example.trendyolfinalproject.model.request.UserRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SellerResponse;
import org.example.trendyolfinalproject.model.response.UserProfileResponse;
import org.example.trendyolfinalproject.model.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    ApiResponse<UserResponse> updateUser(UserRequest userRequest);

    ApiResponse<UserResponse> patchUpdateUser(UserRequest userRequest);

    ApiResponse<String> updateEmail(String newEmail);

    ApiResponse<String> verifyEmail(String newEmail, String otp);

    ApiResponse<String> deleteUser();

    ApiResponse<UserProfileResponse> getUserProfile();

    ApiResponse<String> deactiveUser(Long userId);

    ApiResponse<Page<UserResponse>> searchUser(String keyword, int page, int size);

    ApiResponse<List<SellerResponse>> getFollowedSellers();

    ApiResponse<String> referTrendyol(String email) throws MessagingException;
}
