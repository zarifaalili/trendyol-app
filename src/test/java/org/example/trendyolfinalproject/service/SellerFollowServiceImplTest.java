package org.example.trendyolfinalproject.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.SellerFollow;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.SellerFollowRepository;
import org.example.trendyolfinalproject.dao.repository.SellerRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SellerFollowResponse;
import org.example.trendyolfinalproject.service.impl.SellerFollowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellerFollowServiceImplTest {

    @Mock
    private SellerFollowRepository sellerFollowRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SellerFollowServiceImpl sellerFollowService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(mock(HttpServletRequest.class));
        RequestContextHolder.setRequestAttributes(attributes);
        when(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId")).thenReturn(1L);
    }

    @Test
    void follow_success() {
        User user = new User();
        user.setId(1L);
        user.setName("John");

        Seller seller = new Seller();
        User sellerUser = new User();
        sellerUser.setName("SellerName");
        seller.setUser(sellerUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(sellerFollowRepository.findBySellerAndFollower(seller, user)).thenReturn(Optional.empty());
        when(sellerFollowRepository.save(any(SellerFollow.class))).thenAnswer(i -> i.getArguments()[0]);

        ApiResponse<String> response = sellerFollowService.follow(2L);

        assertEquals(200, response.getStatus());
        assertTrue(response.getData().contains("SellerName"));
        verify(auditLogService, times(1)).createAuditLog(user, "follow", "followed seller");
        verify(notificationService, times(1)).sendNotification(user, "You have new follower John", NotificationType.NEW_FOLLOWER, user.getId());
    }

    @Test
    void follow_fail_alreadyFollowed() {
        User user = new User();
        user.setId(1L);

        Seller seller = new Seller();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(sellerFollowRepository.findBySellerAndFollower(seller, user)).thenReturn(Optional.of(new SellerFollow()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> sellerFollowService.follow(2L));
        assertEquals("You are already following this seller", ex.getMessage());
    }

    @Test
    void unfollow_success() {
        User user = new User();
        user.setId(1L);
        user.setName("John");

        Seller seller = new Seller();
        User sellerUser = new User();
        sellerUser.setName("SellerName");
        seller.setUser(sellerUser);

        SellerFollow follow = SellerFollow.builder().seller(seller).follower(user).followedAt(LocalDateTime.now()).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(sellerFollowRepository.findBySellerAndFollower(seller, user)).thenReturn(Optional.of(follow));

        ApiResponse<String> response = sellerFollowService.unfollow(2L);

        assertEquals(200, response.getStatus());
        assertTrue(response.getData().contains("SellerName"));
        verify(sellerFollowRepository, times(1)).delete(follow);
        verify(auditLogService, times(1)).createAuditLog(user, "unfollow", "unfollowed seller");
    }

    @Test
    void getAllFollowers_success() {
        User user = new User();
        user.setId(1L);

        Seller seller = new Seller();
        seller.setUser(user);

        SellerFollow follow = SellerFollow.builder()
                .seller(seller)
                .follower(new User(){{
                    setName("FollowerName");
                }})
                .followedAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.findByUserId(1L)).thenReturn(Optional.of(seller));
        when(sellerFollowRepository.findBySeller(seller)).thenReturn(List.of(follow));

        ApiResponse<List<SellerFollowResponse>> response = sellerFollowService.getAllFollowers();

        assertEquals(200, response.getStatus());
        assertEquals(1, response.getData().size());
        assertEquals("FollowerName", response.getData().get(0).getFollower());
    }

    @Test
    void follow_fail_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> sellerFollowService.follow(2L));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void follow_fail_sellerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(sellerRepository.findById(2L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> sellerFollowService.follow(2L));
        assertTrue(ex.getMessage().contains("Seller not found"));
    }



    @Test
    void unfollow_fail_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> sellerFollowService.unfollow(2L));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void unfollow_fail_sellerNotFound() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.findById(2L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> sellerFollowService.unfollow(2L));
        assertTrue(ex.getMessage().contains("Seller not found"));
    }

    @Test
    void unfollow_fail_notFollowing() {
        User user = new User();
        Seller seller = new Seller();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(sellerFollowRepository.findBySellerAndFollower(seller, user)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> sellerFollowService.unfollow(2L));
        assertTrue(ex.getMessage().contains("You are not following this seller"));
    }

    @Test
    void getAllFollowers_fail_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> sellerFollowService.getAllFollowers());
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void getAllFollowers_fail_sellerNotFound() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.findByUserId(1L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> sellerFollowService.getAllFollowers());
        assertTrue(ex.getMessage().contains("Seller not found"));
    }

}
