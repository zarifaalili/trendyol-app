package org.example.trendyolfinalproject.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.example.trendyolfinalproject.request.AuthRequest;
import org.example.trendyolfinalproject.request.RefreshTokenRequest;
import org.example.trendyolfinalproject.response.AuthResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final org.example.trendyolfinalproject.util.JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BlacklistService blacklistService;

    public AuthResponse authenticate(AuthRequest req) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));


        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive()) {
            throw new RuntimeException("User is deactivated");
        }


        String token = jwtUtil.generateAccessToken(username, user.getId());
        String refresh = jwtUtil.generateRefreshToken(username, user.getId());
        return new AuthResponse(token, refresh);
    }


    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        if (jwtUtil.isTokenExpired(refreshTokenRequest.getRefreshToken())) {
            throw new RuntimeException("Refresh token is expired");
        }
        var username = jwtUtil.extractUsername(refreshTokenRequest.getRefreshToken());
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive()) {
            throw new RuntimeException("User is deactivated");
        }

        var token = jwtUtil.generateAccessToken(username, user.getId());
        var refreshToken = jwtUtil.generateRefreshToken(username, user.getId());
        return new AuthResponse(token, refreshToken);
    }

//    public String logout(String token) {
//        long expirationMillis = jwtUtil.getExpirationMillis(token);
//        blacklistService.add(token, expirationMillis);
//        return "Logged out successfully!";
//    }
//
//    public boolean isTokenValid(String token) {
//        return !blacklistService.isBlacklisted(token) && !jwtUtil.isTokenExpired(token);
//    }
}
