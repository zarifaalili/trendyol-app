package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.model.NotificationType;
import org.example.trendyolfinalproject.model.Role;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.example.trendyolfinalproject.response.AuditLogResponse;
import org.example.trendyolfinalproject.response.SalesReportResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final OrderService orderService;
    private final PaymentMethodService paymentMethodService;
    private final OrderItemRepository orderItemRepository;
    private final UserCouponRepository userCouponRepository;
    private final OrderRepository orderRepository;
    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;


    public ApiResponse<String> approveSeller(Long sellerId) {
        log.info("Actionlog.approveSeller.start : sellerId={}", sellerId);

        Long currentUserId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");

        var admin = userRepository.findById(currentUserId).orElseThrow(() -> new NotFoundException("User not found"));

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("you cant approve seller. You are not admin");
        }

        var seller = sellerRepository.findById(sellerId).orElseThrow(() -> new NotFoundException("Seller not found"));
        if (seller.getStatus().equals(Status.ACTIVE)) {
            throw new RuntimeException("Seller is already active");
        }
        var user = seller.getUser();
        if (seller.getStatus().equals(Status.PENDING)) {
            seller.setStatus(Status.ACTIVE);
            user.setIsActive(true);
            sellerRepository.save(seller);
            userRepository.save(user);
        }
        notificationService.sendNotification(user, "Your seller account has been approved", NotificationType.SELLER_APPROVED, sellerId);

        log.info("Actionlog.approveSeller.end : sellerId={}", sellerId);
        return new ApiResponse<>(200, "Seller approved successfully", null);

    }


    public ApiResponse<String> rejectSeller(Long sellerId) {
        log.info("Actionlog.rejectSeller.start : sellerId={}", sellerId);
        Long currentUserId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");

        var admin = userRepository.findById(currentUserId).orElseThrow(() -> new NotFoundException("User not found"));

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("you cant reject seller. You are not admin");
        }
        var seller = sellerRepository.findById(sellerId).orElseThrow(() -> new NotFoundException("Seller not found"));
        if (seller.getStatus().equals(Status.ACTIVE)) {
            throw new RuntimeException("Seller is already active");
        }

        var user = seller.getUser();
        if (seller.getStatus().equals(Status.PENDING)) {
            seller.setStatus(Status.REJECTED);
            user.setIsActive(false);
            userRepository.save(user);
            sellerRepository.save(seller);
        }
        notificationService.sendNotification(user, "Your seller account has been rejected", NotificationType.SELLER_REJECTED, sellerId);

        log.info("Actionlog.rejectSeller.end : sellerId={}", sellerId);
        return new ApiResponse<>(200, "Seller rejected successfully", null);
    }


    public ApiResponse<List<User>> getAllAdmins() {
        log.info("Actionlog.getAllAdmins.start : ");
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);

        if (admins.isEmpty()) {
            throw new NotFoundException("No admins found");
        }
        log.info("Actionlog.getAllAdmins.end : ");
        return new ApiResponse<>(200, "Admins fetched successfully", admins);
    }


    public void paySellersForToday() {
        Map<Seller, BigDecimal> earnings = orderService.calculateSellerEarningsToday();

        if (earnings.isEmpty()) {
            return;
        }

        paymentMethodService.payToSellers(earnings);
    }


    public ApiResponse<SalesReportResponse> getSalesReport(LocalDateTime since) {
        log.info("Actionlog.getSalesReport.start : ");

        var totalRevenue = orderItemRepository.getTotalRevenue();
        var totalCouponsUsed = userCouponRepository.getTotalCouponsUsed();
        var product = orderItemRepository.findMostSoldProduct(PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);

        var activeUsers = orderRepository.countActiveUsers(since);

        var salesReport = new SalesReportResponse(totalRevenue, totalCouponsUsed.longValue(), product, activeUsers);
        log.info("Actionlog.getSalesReport.end : ");
        return new ApiResponse<>(200, "Sales report fetched successfully", salesReport);
    }


    public ApiResponse<List<AuditLogResponse>> getUserAuditLogs(Long userId) {
        log.info("Actionlog.getUserAuditLogs.start : userId={}", userId);
        var curentUserId = getCurrentUserId();
        var currentUser = userRepository.findById(curentUserId).orElseThrow(() -> new NotFoundException("User not found"));

        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        var auditLogs = auditLogRepository.findByUserId(user);

        if (auditLogs.isEmpty()) {
            throw new NotFoundException("Audit logs not found");
        }

        var response = auditLogs.stream()
                .map(auditLog -> AuditLogResponse.builder()
                        .userId(auditLog.getUserId())
                        .action(auditLog.getAction())
                        .details(auditLog.getDetails())
                        .actionTime(auditLog.getCreatedAt())
                        .build()
                )
                .toList();


        auditLogService.createAuditLog(currentUser, "Get user audit logs", "User id: " + user.getId());
        log.info("Actionlog.getUserAuditLogs.end : userId={}", userId);
        return new ApiResponse<>(200, "User audit logs fetched successfully", response);
    }


    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }


}