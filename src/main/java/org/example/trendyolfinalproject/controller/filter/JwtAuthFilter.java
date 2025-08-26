package org.example.trendyolfinalproject.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.service.CustomUserDetailsService;
import org.example.trendyolfinalproject.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetails;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (!jwtUtil.isTokenValid(token)) {
                throw new RuntimeException("Token is not valid");
            }
            if (jwtUtil.isTokenExpired(token)) {
                throw new RuntimeException("Token is expired");
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtUtil.extractUsername(token);


                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if(!userDetails.isEnabled()) {
                    throw new RuntimeException("User is disabled");
                }

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

                Long userId = jwtUtil.extractUserId(token);
                request.setAttribute("userId", userId);
            }
        }

        chain.doFilter(request, response);
    }
}