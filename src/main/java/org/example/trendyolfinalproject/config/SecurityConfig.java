package org.example.trendyolfinalproject.config;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.controller.filter.JwtAuthFilter;
import org.example.trendyolfinalproject.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)

public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/sign-up",
                                "/auth/token",
                                "/auth/token/refresh",
                                "/auth/forgot-password",
                                "/auth/verify-code",
                                "/auth/reset-password",
                                "/v1/user/signUp/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v1/product/getProducts",
                                "/v1/product/getProductByName/**",
                                "/v1/seller/createSeller",
                                "/v1/brand/getBrandbyName/**",
                                "/v1/review/getAverageRating/**",
                                "/v1/review/getReviewsByProductId/**",
                                "/v1/productVariant/getProductVariant/**",
                                "/v1/basketElement/createBasketElement",
                                "/v1/wishList/createWishList",
                                "/v1/product-Question/createProductQuestion",
                                "/v1/productVariant/filter",
                                "/v1/user/activateUser/**",
                                "/v1/user/verifyReactivateOtp/**",
                                "/v1/productVariant/getProductVariantDetails/**",
                                "/v1/productVariant/createProductVariant/**",
                                "/v1/review/getReviewsByProductId/**"
                                ).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                ))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(daoProvider())
                .build();
    }


    @Bean
    public DaoAuthenticationProvider daoProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


