package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.request.AuthRequest;
import org.example.trendyolfinalproject.request.RefreshTokenRequest;
import org.example.trendyolfinalproject.response.AuthResponse;
import org.example.trendyolfinalproject.service.AuthService;
import org.example.trendyolfinalproject.service.AuthenticationManager;
import org.example.trendyolfinalproject.service.PasswordResetService;
import org.example.trendyolfinalproject.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
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



//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
//        String token = authHeader.replace("Bearer ", "");
//        String message = authService.logout(token);
//        return ResponseEntity.ok(message);
//    }





}
