package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.AuthRequest;
import org.example.trendyolfinalproject.model.request.RefreshTokenRequest;
import org.example.trendyolfinalproject.model.request.UserRegisterRequest;
import org.example.trendyolfinalproject.model.request.VerifyAndRegisterRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.AuthResponse;
import org.example.trendyolfinalproject.service.AuthService;
import org.example.trendyolfinalproject.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService resetService;

    @PostMapping("/token")
    public AuthResponse token(@RequestBody @Valid AuthRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/token/refresh")
    public AuthResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/forgot-password")
    public void forgot(@RequestParam String email) {
        resetService.sendCode(email);
    }


    @PostMapping("/verify-code")
    public void verify(@RequestParam String email, @RequestParam String code) {
        resetService.verifyCode(email, code);
    }


    @PostMapping("/reset-password")
    public void reset(@RequestParam String email,
                      @RequestParam String code,
                      @RequestParam String newPassword,
                      @RequestParam String confirmPassword) {
        resetService.resetPassword(email, code, newPassword, confirmPassword);
    }


    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<String>> registerOrLoginUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        ApiResponse<String> response = authService.registerUser(userRegisterRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PostMapping("/signUp/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @RequestBody @Valid VerifyAndRegisterRequest verifyRequest) {
        ApiResponse<AuthResponse> response = authService.verifyOtp(verifyRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/user-activate")
    public ApiResponse<String> activeUser(@PathParam("email") String email) {
        return ApiResponse.success(authService.activateUser(email));
    }

    @PatchMapping("/verify-reactivate-otp")
    public ApiResponse<String> verifyReactivateOtp(@PathParam("email") String email, @RequestParam("otp") String otp) {
        return ApiResponse.success(authService.verifyReactivateOtp(email, otp));
    }


}
