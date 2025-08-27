package org.example.trendyolfinalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Basket;
import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.EmailAlreadyExistsException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.exception.customExceptions.VerifyEmailException;
import org.example.trendyolfinalproject.mapper.BasketMapper;
import org.example.trendyolfinalproject.mapper.UserMapper;
import org.example.trendyolfinalproject.model.NotificationType;
import org.example.trendyolfinalproject.model.Role;
import org.example.trendyolfinalproject.request.UserRegisterRequest;
import org.example.trendyolfinalproject.request.UserRequest;
import org.example.trendyolfinalproject.response.*;
import org.example.trendyolfinalproject.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BasketRepository basketRepository;
    private final BasketMapper basketMapper;
    private final PaymentMethodRepository paymentMethodRepository;
    private final WishlistRepository wishlistRepository;
    private final OrderRepository orderRepository;
    private final SellerFollowRepository sellerFollowRepository;
    private PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;
    private final AdressRepository adressRepository;


    private final ConcurrentMap<String, String> otpStore = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> otpExpiry = new ConcurrentHashMap<>();


    public ApiResponse<String> registerUser(UserRegisterRequest userRegisterRequest) {
        log.info("Actionlog.registerUser.start : ");
        var existingUser = userRepository.findByEmail(userRegisterRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("User already exists with this email " + userRegisterRequest.getEmail());
        }

        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getConfirmedPassword())) {
            throw new VerifyEmailException("Password and confirmed password do not match");
        }

        String otp = generateOtp();
        Long expiry = System.currentTimeMillis() + 5 * 60 * 1000;

        otpStore.put(userRegisterRequest.getEmail(), otp);
        otpExpiry.put(userRegisterRequest.getEmail(), expiry);

        emailService.sendOtp(userRegisterRequest.getEmail(), otp);
        log.info("Actionlog.registerUser.end : ");
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("OTP sent successfully")
                .data("Otp sent to " + userRegisterRequest.getEmail())
                .build();
    }

    public ApiResponse<AuthResponse> verifyOtp(String email, String otp, UserRegisterRequest userRegisterRequest) { //
        log.info("Actionlog.verifyOtp.start : ");
        String storedOtp = otpStore.get(email);
        Long expiry = otpExpiry.get(email);


        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new VerifyEmailException("Invalid OTP");
        }

        if (System.currentTimeMillis() > expiry) {
            throw new VerifyEmailException("OTP has expired");
        }

        otpStore.remove(email);
        otpExpiry.remove(email);

        var user = userMapper.toEntity(userRegisterRequest);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setRole(Role.CUSTOMER);

        var savedUser = userRepository.save(user);

        var basket = new Basket();
        basket.setUser(savedUser);
        basket.setCreatedAt(LocalDateTime.now());
        basket.setUpdatedAt(LocalDateTime.now());
        basketRepository.save(basket);

        auditLogService.createAuditLog(savedUser, "Sign up", "User registered successfully. User id: " + savedUser.getId());

        String accessToken = jwtUtil.generateAccessToken(email, savedUser.getId());
        String refreshToken = jwtUtil.generateRefreshToken(email, savedUser.getId());
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

    public ApiResponse<UserResponse> updateUser(UserRequest userRequest) {
        log.info("Actionlog.updateUser.start : ");

        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        var now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(user.getUpdatedAt(), now);

        if (daysBetween < 7) {
            throw new RuntimeException("You can update your info once a week");
        }


        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setPhoneNumber("(+994) " + userRequest.getPhoneNumber());
        var saved = userRepository.save(user);
        var response = userMapper.toUserResponse(saved);

        auditLogService.createAuditLog(user, "Update User", "User updated own info successfully. User id: " + user.getId());
        notificationService.sendNotification(user, "Your account updated  successfully. Your account name " + user.getName(), NotificationType.USER_UPDATE, user.getId());

        log.info("Actionlog.updateUser.end : ");
        return ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User updated successfully")
                .data(response)
                .build();
    }


    public ApiResponse<UserResponse> patchUpdateUser(UserRequest userRequest) {
        log.info("Actionlog.patchUpdateUser.start : ");

        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (userRequest.getName() != null) {
            user.setName(userRequest.getName());
        }

        if (userRequest.getSurname() != null) {
            user.setSurname(userRequest.getSurname());
        }

        if (userRequest.getPhoneNumber() != null) {
            user.setPhoneNumber("(+994) " + userRequest.getPhoneNumber());
        }
        if (userRequest.getDateOfBirth() != null) {
            user.setDateOfBirth(LocalDate.parse(userRequest.getDateOfBirth()));
        }

        var saved = userRepository.save(user);
        var response = userMapper.toUserResponse(saved);

        auditLogService.createAuditLog(user, "Update User", "User updated own info successfully. User id: " + user.getId());
        notificationService.sendNotification(user, "Your account updated  successfully. Your account name " + user.getName(), NotificationType.USER_UPDATE, user.getId());

        log.info("Actionlog.patchUpdateUser.end : ");

        return ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User updated successfully")
                .data(response)
                .build();
    }


    public ApiResponse<String> updateEmail(String newEmail) {
        log.info("Actionlog.updateEmail.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var exist = userRepository.findByEmail(newEmail).orElse(null);
        if (exist != null) {
            throw new AlreadyException("Email already exists");
        }

        if (user.getEmail().equals(newEmail)) {
            throw new AlreadyException("Email already exists");
        }
        String otp = generateOtp();
        Long expiry = System.currentTimeMillis() + 5 * 60 * 1000;

        otpStore.put(newEmail, otp);
        otpExpiry.put(newEmail, expiry);
        emailService.sendOtp(newEmail, otp);

        log.info("Actionlog.updateEmail.end : ");

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Otp sent for email verification")
                .data("Otp sent to " + newEmail)
                .build();
    }


    public ApiResponse<String> verifyEmail(String newEmail, String otp) {

        log.info("Actionlog.verifyEmail.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        var otpFromStore = otpStore.get(newEmail);
        var expiry = otpExpiry.get(newEmail);

        if (otpFromStore == null || !otpFromStore.equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        if (expiry < System.currentTimeMillis()) {
            throw new RuntimeException("OTP expired");
        }
        otpStore.remove(newEmail);
        otpExpiry.remove(newEmail);
        String oldEmail = user.getEmail();

        user.setEmail(newEmail);
        user.setUpdatedAt(LocalDateTime.now());
        var saved = userRepository.save(user);

        auditLogService.createAuditLog(user, "Update User Email", "User updated own info successfully. User id: " + user.getId());
        emailService.sendEmail(user.getEmail(), "Update Account Info", "Your account updated  successfully. Your account name " + user.getName());
        notificationService.sendNotification(user, "Your email has been updated!", NotificationType.EMAIL_UPDATE, user.getId());
        emailService.sendEmail(oldEmail, "Update Account Info", "Your email has been updated to " + newEmail);
        emailService.sendEmail(newEmail, "Your email address has been set as your email address on Trendyol. User name: ", user.getName());
        log.info("Actionlog.verifyEmail.end : ");

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Email updated successfully")
                .data("Email changed from " + oldEmail + " to " + newEmail)
                .build();
    }


    public ApiResponse<String> deleteUser() {
        log.info("Actionlog.deleteUser.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("Actionlog.deleteUser.end : ");

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("User deleted successfully")
                .data("User with id " + user.getId() + " deactivated")
                .build();
    }

    public ApiResponse<UserProfileResponse> getUserProfile() {
        log.info("Actionlog.getUserProfile.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var response = userMapper.toUserProfileResponse(user);

        var adress = adressRepository.findByUserId(user);
        response.setAddresses(adress);

        var defaultPaymentMethod = paymentMethodRepository.findByUserId_IdAndIsDefault(user.getId(), true);
        response.setDefaultPaymentMethod(defaultPaymentMethod.get());


        var wishlistCount = wishlistRepository.findByUser(user).size();
        response.setWishlistCount(wishlistCount);

        var orderCount = orderRepository.findByUserId_Id(userId).size();
        response.setOrderCount(orderCount);

        var totalSpent = calculateTotalSpent(userId);
        response.setTotalSpent(totalSpent);

        log.info("Actionlog.getUserProfile.end : ");
        return ApiResponse.<UserProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User profile retrieved successfully")
                .data(response)
                .build();
    }

    private Double calculateTotalSpent(Long userId) {
        List<Order> orders = orderRepository.findByUserId_Id(userId);
        BigDecimal totalSpent = BigDecimal.ZERO;
        for (Order order : orders) {
            totalSpent = totalSpent.add(order.getTotalAmount());
        }
        return totalSpent.doubleValue();
    }


    public String deactiveUser() {
        log.info("Actionlog.deactiveUser.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        if (!user.getIsActive()) {
            throw new RuntimeException("User is already deactive");
        }
        user.setIsActive(false);
        userRepository.save(user);
        auditLogService.createAuditLog(user, "Deactive User", "User deactive successfully. User id: " + user.getId());
        emailService.sendEmail(user.getEmail(), "Deactive Account Info", "Your account deactive successfully. Your account name " + user.getName());
        notificationService.sendNotification(user, "Your account deactive successfully. Your account name " + user.getName(), NotificationType.USER_DEACTIVE, user.getId());
        log.info("Actionlog.deactiveUser.end : ");
        return "User deactive successfully";
    }


    public String activateUser(String email) {
        log.info("Actionlog.activateUser.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        if (user.getIsActive()) {
            throw new RuntimeException("User is already active");
        }
        String otp = generateOtp();
        Long expiry = System.currentTimeMillis() + 5 * 60 * 1000;
        otpStore.put(email, otp);
        otpExpiry.put(email, expiry);
        emailService.sendOtp(email, otp);
        log.info("Actionlog.activateUser.end : ");
        return "We sent otp to your email to activate your account";
    }

    public String verifyReactivateOtp(String email, String otp) {
        log.info("Actionlog.verifyReactivateOtp.start : ");
        var user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found with id: " + email));
        var otpFromStore = otpStore.get(email);
        var expiry = otpExpiry.get(email);
        if (otpFromStore == null || !otpFromStore.equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        if (expiry < System.currentTimeMillis()) {
            throw new RuntimeException("OTP expired");
        }
        otpStore.remove(email);
        otpExpiry.remove(email);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("Actionlog.verifyReactivateOtp.end : ");
        return "User reactivate successfully";

    }


    public ApiResponse<List<UserResponse>> searchUser(String keyword) {
        log.info("Actionlog.searchUser.start : ");
        var users = userRepository.searchByKeyword(keyword);
        if (users.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        log.info("Actionlog.searchUser.end : ");
        var response = userMapper.toResponseList(users);
        return ApiResponse.<List<UserResponse>>builder()
                .data(response)
                .status(HttpStatus.OK.value())
                .message("Search User").
                build();
    }


    public ApiResponse<List<SellerResponse>> getFollowedSellers() {
        log.info("Actionlog.getFollowedSellers.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var followedSellers = sellerFollowRepository.findByFollower(user);

        var response = followedSellers.stream()
                .map(f -> SellerResponse.builder()
                        .taxId(f.getSeller().getTaxId())
                        .companyName(f.getSeller().getCompanyName())
                        .contactEmail(f.getSeller().getContactEmail())
                        .build())
                .toList();

        log.info("Actionlog.getFollowedSellers.end : ");
        return ApiResponse.<List<SellerResponse>>builder()
                .data(response)
                .status(HttpStatus.OK.value())
                .message("Followed Sellers").
                build();
    }


    public ApiResponse<String> referTrendyol(String email) {
        log.info("Actionlog.referTrendyol.start : ");
        var user=userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + getCurrentUserId()));
        emailService.sendEmail(email, "Refer Trendyol", "https://www.trendyol.com/");
        log.info("Actionlog.referTrendyol.end : ");
        return ApiResponse.<String>builder()
                .data("Refer Trendyol")
                .status(HttpStatus.OK.value())
                .message("Trendyol reffered successfully").
                build();

    }


//    public UserResponse


//
//    public UserResponse createOrLoginUser(UserRegisterRequest userRegisterRequest) {
//        log.info("Actionlog.createOrLoginUser.start : email={}", userRegisterRequest.getEmail());
//        Optional<User> findUserByEmail = userRepository.findByEmail(userRegisterRequest.getEmail());
//        if (findUserByEmail.isPresent()) {
//            User user = findUserByEmail.get();
//            var response=UserMapper.INSTANCE.toUserResponse(user);
//
//            auditLogService.createAuditLog(user, "Login", "User logged in successfully. User id: " + user.getId());
//
//            log.info("Actionlog.createOrLoginUser.end : email={}", userRegisterRequest.getEmail());
//            return response;
//        } else {
//            String otp = generateOtp();
//            Long expiry = System.currentTimeMillis() + 60000;
//            otpStore.put(userRegisterRequest.getEmail(), otp);
//            otpExpiry.put(userRegisterRequest.getEmail(), expiry);
//            emailService.sendOtp(userRegisterRequest.getEmail(), otp);
//            log.info("Actionlog.createOrLoginUser.end : createdNewEmail={}", userRegisterRequest.getEmail());
//            throw new VerifyEmailException("OTP sent to your email. Please verify.");
//        }
//
//    }

//    public UserResponse verifyOtp(String email, String otp, UserRegisterRequest userRegisterRequest) {
//        log.info("Actionlog.verifyOtp.start : email={}", email);
//        if (otp != null) {
//            otp = otp.trim();
//        }
//
//        String storedOtp = otpStore.get(email);
//        Long expiry = otpExpiry.get(email);
//
//        if (storedOtp == null || !storedOtp.equals(otp) || System.currentTimeMillis() > expiry) {
//            throw new RuntimeException("Invalid OTP");
//        }
//        if (System.currentTimeMillis() > expiry) {
//            otpStore.remove(email);
//            otpExpiry.remove(email);
//        }
//

    /// /
    /// /        User user = UserMapper.INSTANCE.toEntity(userRegisterRequest);
    /// /        var saved = userRepository.save(user);
//
//        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getConfirmedPassword())) {
//            throw new RuntimeException("Passwords do not match");
//        }
//
//        User user = new User();
//        user.setName(userRegisterRequest.getName());
//        user.setSurname(userRegisterRequest.getSurname());
//        user.setEmail(email);
//        user.setPasswordHash(passwordEncoder.encode(userRegisterRequest.getPassword())); // parol hash ilə
//        user.setPhoneNumber(userRegisterRequest.getPhoneNumber());
//        user.setRole(userRegisterRequest.getRole() != null ? userRegisterRequest.getRole() : Role.CUSTOMER);
//        user.setIsActive(true);
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//
//        User saved = userRepository.save(user);
//
//        Basket basket = new Basket();
//        basket.setUser(saved);
//        basket.setCreatedAt(LocalDateTime.now());
//        basket.setUpdatedAt(LocalDateTime.now());
//        basketRepository.save(basket);
//
//        otpStore.remove(email);
//        otpExpiry.remove(email);
//        auditLogService.createAuditLog(saved, "Sign up", "User sign up successfully. User id: " + saved.getId());
//
//        var response=UserMapper.INSTANCE.toUserResponse(saved);
//        log.info("Actionlog.verifyOtp.end : email={}", email);
//        return response;
//    }
    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
