package org.example.trendyolfinalproject.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.enums.Role;
import org.example.trendyolfinalproject.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private OrderService orderService;
    @Mock
    private PaymentMethodService paymentMethodService;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(mock(HttpServletRequest.class));
        RequestContextHolder.setRequestAttributes(attributes);
        when(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("userId"))
                .thenReturn(1L);
    }


    @Test
    void approveSeller_success() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        User sellerUser = new User();
        sellerUser.setId(2L);
        sellerUser.setIsActive(false);

        Seller seller = new Seller();
        seller.setId(10L);
        seller.setStatus(Status.PENDING);
        seller.setUser(sellerUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(sellerRepository.findById(10L)).thenReturn(Optional.of(seller));

        var response = adminService.approveSeller(10L);

        assertEquals(200, response.getStatus());
        assertEquals("Seller approved successfully", response.getMessage());
        verify(notificationService, times(1))
                .sendNotification(sellerUser, "Your seller account has been approved",
                        NotificationType.SELLER_APPROVED, 10L);
    }

    @Test
    void approveSeller_fail_notAdmin() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.SELLER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.approveSeller(5L));

        assertEquals("you cant approve seller. You are not admin", ex.getMessage());
    }

    @Test
    void approveSeller_fail_sellerNotFound() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(sellerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminService.approveSeller(99L));
    }

    @Test
    void approveSeller_fail_sellerAlreadyActive() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Seller seller = new Seller();
        seller.setStatus(Status.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(sellerRepository.findById(2L)).thenReturn(Optional.of(seller));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.approveSeller(2L));
        assertEquals("Seller is already active", ex.getMessage());
    }


    @Test
    void rejectSeller_success() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        User sellerUser = new User();
        sellerUser.setIsActive(true);

        Seller seller = new Seller();
        seller.setId(2L);
        seller.setStatus(Status.PENDING);
        seller.setUser(sellerUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(sellerRepository.findById(2L)).thenReturn(Optional.of(seller));

        var response = adminService.rejectSeller(2L);

        assertEquals(200, response.getStatus());
        assertEquals("Seller rejected successfully", response.getMessage());
        verify(notificationService, times(1))
                .sendNotification(sellerUser, "Your seller account has been rejected",
                        NotificationType.SELLER_REJECTED, 2L);
    }

    @Test
    void rejectSeller_fail_notAdmin() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.rejectSeller(5L));
        assertEquals("you cant reject seller. You are not admin", ex.getMessage());
    }

    @Test
    void rejectSeller_fail_sellerNotFound() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(sellerRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminService.rejectSeller(9L));
    }


    @Test
    void getAllAdmins_success() {
        List<User> admins = List.of(new User(), new User());
        when(userRepository.findAllByRole(Role.ADMIN)).thenReturn(admins);

        var response = adminService.getAllAdmins();

        assertEquals(200, response.getStatus());
        assertEquals(2, response.getData().size());
    }

    @Test
    void getAllAdmins_fail_noAdmins() {
        when(userRepository.findAllByRole(Role.ADMIN)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> adminService.getAllAdmins());
    }


    @Test
    void paySellersForToday_success() {
        Map<Seller, BigDecimal> earnings = Map.of(new Seller(), BigDecimal.TEN);
        when(orderService.calculateSellerEarningsToday()).thenReturn(earnings);

        adminService.paySellersForToday();

        verify(paymentMethodService, times(1)).payToSellers(earnings);
    }

    @Test
    void paySellersForToday_emptyEarnings() {
        when(orderService.calculateSellerEarningsToday()).thenReturn(Collections.emptyMap());

        adminService.paySellersForToday();

        verify(paymentMethodService, never()).payToSellers(any());
    }


    @Test
    void getSalesReport_success() {
        when(orderItemRepository.getTotalRevenue()).thenReturn(BigDecimal.valueOf(2000));
        when(userCouponRepository.getTotalCouponsUsed()).thenReturn(BigDecimal.valueOf(5));
        when(orderItemRepository.findMostSoldProduct(PageRequest.of(0, 1)))
                .thenReturn(List.of("Laptop"));
        when(orderRepository.countActiveUsers(any())).thenReturn(10L);

        var response = adminService.getSalesReport(LocalDateTime.now());

        assertEquals(200, response.getStatus());
        assertEquals(2000, response.getData().getTotalRevenue().intValue());
        assertEquals("Laptop", response.getData().getMostSoldProduct());
    }


    @Test
    void getUserAuditLogs_success() {
        User admin = new User();
        admin.setId(1L);

        User target = new User();
        target.setId(2L);

        var logEntity = new org.example.trendyolfinalproject.dao.entity.AuditLog();
        logEntity.setUserId(target);
        logEntity.setAction("Test Action");
        logEntity.setDetails("Test Details");
        logEntity.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(auditLogRepository.findByUserId(target)).thenReturn(List.of(logEntity));

        var response = adminService.getUserAuditLogs(2L);

        assertEquals(200, response.getStatus());
        assertEquals(1, response.getData().size());
        verify(auditLogService, times(1))
                .createAuditLog(admin, "Get user audit logs", "User id: " + target.getId());
    }

    @Test
    void getUserAuditLogs_fail_noLogs() {
        User admin = new User();
        admin.setId(1L);
        User target = new User();
        target.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(auditLogRepository.findByUserId(target)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> adminService.getUserAuditLogs(2L));
    }
}
