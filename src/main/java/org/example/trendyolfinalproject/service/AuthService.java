package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.AuthRequest;
import org.example.trendyolfinalproject.model.request.RefreshTokenRequest;
import org.example.trendyolfinalproject.model.request.UserRegisterRequest;
import org.example.trendyolfinalproject.model.request.VerifyAndRegisterRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.AuthResponse;

public interface AuthService {

    AuthResponse authenticate(AuthRequest req);

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    ApiResponse<String> registerUser(UserRegisterRequest userRegisterRequest);

    ApiResponse<AuthResponse> verifyOtp(VerifyAndRegisterRequest verifyRequest);

    String activateUser(String email);

    String verifyReactivateOtp(String email, String otp);


}
