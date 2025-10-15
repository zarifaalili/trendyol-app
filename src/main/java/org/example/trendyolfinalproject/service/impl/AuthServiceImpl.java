package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.client.EmailClient;
import org.example.trendyolfinalproject.dao.entity.Basket;
import org.example.trendyolfinalproject.dao.entity.ResetCode;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.EmailAlreadyExistsException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.exception.customExceptions.VerifyEmailException;
import org.example.trendyolfinalproject.mapper.UserMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.enums.Role;
import org.example.trendyolfinalproject.model.request.AuthRequest;
import org.example.trendyolfinalproject.model.request.RefreshTokenRequest;
import org.example.trendyolfinalproject.model.request.UserRegisterRequest;
import org.example.trendyolfinalproject.model.request.VerifyAndRegisterRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.AuthResponse;
import org.example.trendyolfinalproject.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authManager;
    private final org.example.trendyolfinalproject.util.JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BlacklistService blacklistService;
    private final UserService userService;
    private final EmailService emailService;
    private final BasketRepository basketRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final WishlistRepository wishlistRepository;
    private final OrderRepository orderRepository;
    private final SellerFollowRepository sellerFollowRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    private final AdressRepository adressRepository;
    private final EmailClient emailClient;
    private final ResetCodeRepository resetCodeRepository;


    @Override
    public AuthResponse authenticate(AuthRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        String username = authentication.getName();
        if (!user.getIsActive()) {
            throw new RuntimeException("User is deactivated");
        }
        String token = jwtUtil.generateAccessToken(username, user.getId());
        String refresh = jwtUtil.generateRefreshToken(username, user.getId());
        return new AuthResponse(token, refresh);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        if (jwtUtil.isTokenExpired(refreshTokenRequest.getRefreshToken())) {
            throw new RuntimeException("Refresh token is expired");
        }
        var username = jwtUtil.extractUsername(refreshTokenRequest.getRefreshToken());
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getIsActive()) {
            throw new RuntimeException("User is deactivated");
        }
        var token = jwtUtil.generateAccessToken(username, user.getId());
        var refreshToken = jwtUtil.generateRefreshToken(username, user.getId());
        return new AuthResponse(token, refreshToken);
    }


    public ApiResponse<String> registerUser(UserRegisterRequest userRegisterRequest) {
        log.info("Actionlog.registerUser.start : ");
        var existingUser = userRepository.findByEmail(userRegisterRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("User already exists with this email " + userRegisterRequest.getEmail());
        }

        var existsEmail = emailClient.checkEmailExists(userRegisterRequest.getEmail());
        if (!existsEmail) {
            throw new RuntimeException("Email is not available");
        }
        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getConfirmedPassword())) {
            throw new VerifyEmailException("Password and confirmed password do not match");
        }

        var existingPhone = userRepository.existsUserByPhoneNumber(userRegisterRequest.getPhoneNumber());

        if (existingPhone) {
            throw new AlreadyException("User already exists with this phone number : " + userRegisterRequest.getPhoneNumber());
        }
        String otp = generateOtp();
        ResetCode resetCode = new ResetCode();
        resetCode.setEmail(userRegisterRequest.getEmail());
        resetCode.setCode(otp);
        resetCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
        resetCodeRepository.save(resetCode);

        emailService.sendOtp(userRegisterRequest.getEmail(), otp);

        log.info("Actionlog.registerUser.end : ");
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("OTP sent successfully")
                .data("Otp sent to " + userRegisterRequest.getEmail())
                .build();
    }

    @Transactional
    public ApiResponse<AuthResponse> verifyOtp(VerifyAndRegisterRequest verifyAndRegisterRequest) {
        log.info("Actionlog.verifyOtp.start : ");

        List<ResetCode> expiredCodes = resetCodeRepository.findAllByEmailAndExpireTimeBefore(verifyAndRegisterRequest.getVerifyRequest().getEmail(), LocalDateTime.now());
        resetCodeRepository.deleteAll(expiredCodes);

        ResetCode resetCode = resetCodeRepository.findByEmailAndCode(verifyAndRegisterRequest.getVerifyRequest().getEmail(), verifyAndRegisterRequest.getVerifyRequest().getCode())
                .orElseThrow(() -> new VerifyEmailException("Invalid OTP"));

        if (resetCode.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new VerifyEmailException("OTP has expired");
        }
        resetCodeRepository.delete(resetCode);

        var user = userMapper.toEntity(verifyAndRegisterRequest.getUserRegisterRequest());
        user.setEmail(verifyAndRegisterRequest.getVerifyRequest().getEmail());
        user.setPasswordHash(passwordEncoder.encode(verifyAndRegisterRequest.getUserRegisterRequest().getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setPhoneNumber(verifyAndRegisterRequest.getUserRegisterRequest().getPhoneNumber());
        user.setDateOfBirth(verifyAndRegisterRequest.getUserRegisterRequest().getDateOfBirth());

        var savedUser = userRepository.save(user);

        var basket = new Basket();
        basket.setUser(savedUser);
        basket.setCreatedAt(LocalDateTime.now());
        basket.setUpdatedAt(LocalDateTime.now());
        basketRepository.save(basket);

        auditLogService.createAuditLog(savedUser, "Sign up", "User registered successfully. User id: " + savedUser.getId());

        String accessToken = jwtUtil.generateAccessToken(verifyAndRegisterRequest.getVerifyRequest().getEmail(), savedUser.getId());
        String refreshToken = jwtUtil.generateRefreshToken(verifyAndRegisterRequest.getVerifyRequest().getEmail(), savedUser.getId());
        auditLogService.createAuditLog(savedUser, "Verify otp", "User verified successfully. User id: " + savedUser.getId());

        notificationService.sendToAdmins("New user registered", NotificationType.USER_REGISTER, savedUser.getId());
        notificationService.sendNotification(savedUser, "Welcome to Trendyol", NotificationType.WELCOME, savedUser.getId());

        log.info("Actionlog.verifyOtp.end : ");
        return ApiResponse.<AuthResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .data(new AuthResponse(accessToken, refreshToken))
                .build();
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }


}
