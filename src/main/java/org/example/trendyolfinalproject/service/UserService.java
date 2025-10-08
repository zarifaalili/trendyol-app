package org.example.trendyolfinalproject.service;

import jakarta.mail.MessagingException;
import org.example.trendyolfinalproject.model.request.UserRegisterRequest;
import org.example.trendyolfinalproject.model.request.UserRequest;
import org.example.trendyolfinalproject.model.response.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    ApiResponse<String> registerUser(UserRegisterRequest userRegisterRequest);

    ApiResponse<AuthResponse> verifyOtp(String email, String otp, UserRegisterRequest userRegisterRequest);

    ApiResponse<UserResponse> updateUser(UserRequest userRequest);

    ApiResponse<UserResponse> patchUpdateUser(UserRequest userRequest);

    ApiResponse<String> updateEmail(String newEmail);

    ApiResponse<String> verifyEmail(String newEmail, String otp);

    ApiResponse<String> deleteUser();

    ApiResponse<UserProfileResponse> getUserProfile();

    String deactiveUser();

    String activateUser(String email);

    String verifyReactivateOtp(String email, String otp);

    ApiResponse<Page<UserResponse>> searchUser(String keyword, int page, int size);

    ApiResponse<List<SellerResponse>> getFollowedSellers();

    ApiResponse<String> referTrendyol(String email) throws MessagingException;
}
