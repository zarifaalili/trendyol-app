package org.example.trendyolfinalproject.controller;

import org.example.trendyolfinalproject.model.request.*;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.AuthResponse;
import org.example.trendyolfinalproject.service.AuthService;
import org.example.trendyolfinalproject.service.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private PasswordResetService resetService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTokenSuccess() {
        AuthRequest request = new AuthRequest("zari@example.com", "123456");
        AuthResponse expected = new AuthResponse("access123", "refresh456");

        when(authService.authenticate(request)).thenReturn(expected);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.token(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().getData());
        verify(authService, times(1)).authenticate(request);
    }

    @Test
    void testTokenFail_invalidCredentials() {
        AuthRequest request = new AuthRequest("zari@example.com", "wrongpass");
        when(authService.authenticate(request)).thenThrow(new RuntimeException("Invalid email or password"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authController.token(request));
        assertEquals("Invalid email or password", ex.getMessage());
        verify(authService, times(1)).authenticate(request);
    }

    @Test
    void testRefreshTokenSuccess() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh456");
        AuthResponse expected = new AuthResponse("newAccess", "refresh456");

        when(authService.refreshToken(request)).thenReturn(expected);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.refresh(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().getData());
        verify(authService, times(1)).refreshToken(request);
    }




    @Test
    void testSignUpSuccess() {
        UserRegisterRequest req = new UserRegisterRequest(
                "Zari",
                "Aliyeva",
                "zari@example.com",
                "12345678",
                "12345678",
                "+994501234567",
                LocalDate.of(2005, 5, 15)
        );

        ApiResponse<String> expected = ApiResponse.successWithMessage("ok", "Registered");

        when(authService.registerUser(req)).thenReturn(expected);

        ResponseEntity<ApiResponse<String>> response = authController.registerOrLoginUser(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ok", response.getBody().getData());
        assertEquals("Registered", response.getBody().getMessage());
        verify(authService, times(1)).registerUser(req);
    }

    @Test
    void testVerifyOtpSuccess() {
        VerifyRequest verifyReq = new VerifyRequest("1234", "zari@example.com");
        UserRegisterRequest userReq = new UserRegisterRequest(
                "Zari",
                "Aliyeva",
                "zari@example.com",
                "12345678",
                "12345678",
                "+994501234567",
                LocalDate.of(2005, 5, 15)
        );

        VerifyAndRegisterRequest req = new VerifyAndRegisterRequest(verifyReq, userReq);

        AuthResponse authRes = new AuthResponse("access", "refresh");
        ApiResponse<AuthResponse> expected = ApiResponse.success(authRes);

        when(authService.verifyOtp(req)).thenReturn(expected);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.verifyOtp(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authRes, response.getBody().getData());
        verify(authService, times(1)).verifyOtp(req);
    }

    @Test
    void testUserActivateSuccess() {
        String email = "zari@example.com";
        when(authService.activateUser(email)).thenReturn("otpSent");

        ResponseEntity<ApiResponse<String>> response = authController.activeUser(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("otpSent", response.getBody().getData());
        assertEquals("we send you an otp", response.getBody().getMessage());
        verify(authService, times(1)).activateUser(email);
    }

    @Test
    void testVerifyReactivateOtpSuccess() {
        String email = "zari@example.com";
        String otp = "9999";
        when(authService.verifyReactivateOtp(email, otp)).thenReturn("verified");

        ResponseEntity<ApiResponse<String>> response = authController.verifyReactivateOtp(email, otp);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("verified", response.getBody().getData());
        assertEquals("otp verified", response.getBody().getMessage());
        verify(authService, times(1)).verifyReactivateOtp(email, otp);
    }
}
