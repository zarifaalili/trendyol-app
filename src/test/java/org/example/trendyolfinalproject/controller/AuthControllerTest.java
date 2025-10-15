package org.example.trendyolfinalproject.controller;

import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.model.request.AuthRequest;
import org.example.trendyolfinalproject.model.request.RefreshTokenRequest;
import org.example.trendyolfinalproject.model.response.AuthResponse;
import org.example.trendyolfinalproject.service.AuthService;
import org.example.trendyolfinalproject.service.AuthenticationManager;
import org.example.trendyolfinalproject.service.PasswordResetService;
import org.example.trendyolfinalproject.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

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
        AuthResponse expectedResponse = new AuthResponse("accessToken123", "refreshToken456");

        when(authService.authenticate(request)).thenReturn(expectedResponse);

        AuthResponse actualResponse = authController.token(request);

        assertEquals(expectedResponse, actualResponse);
        verify(authService, times(1)).authenticate(request);
    }

    @Test
    void testTokenFail_invalidCredentials() {
        AuthRequest request = new AuthRequest("zari@example.com", "wrongpass");

        when(authService.authenticate(request))
                .thenThrow(new RuntimeException("Invalid email or password"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authController.token(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(authService, times(1)).authenticate(request);
    }

    @Test
    void testRefreshTokenSuccess() {
        RefreshTokenRequest request = new RefreshTokenRequest("refreshToken456");
        AuthResponse expectedResponse = new AuthResponse("newAccessToken", "refreshToken456");

        when(authService.refreshToken(request)).thenReturn(expectedResponse);

        AuthResponse actualResponse = authController.refresh(request);

        assertEquals(expectedResponse, actualResponse);
        verify(authService, times(1)).refreshToken(request);
    }

    @Test
    void testRefreshTokenFail_nullToken() {
        RefreshTokenRequest request = new RefreshTokenRequest(null);

        when(authService.refreshToken(request))
                .thenThrow(new RuntimeException("Refresh token cannot be null"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authController.refresh(request)
        );

        assertEquals("Refresh token cannot be null", exception.getMessage());
        verify(authService, times(1)).refreshToken(request);
    }

    @Test
    void testForgotPasswordSuccess() {
        String email = "zari@example.com";

        doNothing().when(resetService).sendCode(email);

        authController.forgot(email);

        verify(resetService, times(1)).sendCode(email);
    }

    @Test
    void testForgotPasswordFail_emailNotFound() {
        String email = "unknown@example.com";

        doThrow(new RuntimeException("Email not found"))
                .when(resetService).sendCode(email);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authController.forgot(email)
        );

        assertEquals("Email not found", exception.getMessage());
        verify(resetService, times(1)).sendCode(email);
    }

    @Test
    void testVerifyCodeSuccess() {
        String email = "zari@example.com";
        String code = "1234";

        doNothing().when(resetService).verifyCode(email, code);

        authController.verify(email, code);

        verify(resetService, times(1)).verifyCode(email, code);
    }

    @Test
    void testResetPasswordSuccess() {
        String email = "zari@example.com";
        String code = "1234";
        String newPassword = "newpass";
        String confirmPassword = "newpass";

        doNothing().when(resetService).resetPassword(email, code, newPassword, confirmPassword);

        authController.reset(email, code, newPassword, confirmPassword);

        verify(resetService, times(1)).resetPassword(email, code, newPassword, confirmPassword);
    }

    @Test
    void testResetPasswordFail_passwordMismatch() {
        String email = "zari@example.com";
        String code = "1234";
        String newPassword = "newpass";
        String confirmPassword = "different";

        doThrow(new RuntimeException("Passwords do not match"))
                .when(resetService).resetPassword(email, code, newPassword, confirmPassword);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authController.reset(email, code, newPassword, confirmPassword)
        );

        assertEquals("Passwords do not match", exception.getMessage());
        verify(resetService, times(1)).resetPassword(email, code, newPassword, confirmPassword);
    }
}
