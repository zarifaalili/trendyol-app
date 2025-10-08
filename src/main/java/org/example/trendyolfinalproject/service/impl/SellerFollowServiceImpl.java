package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.SellerFollow;
import org.example.trendyolfinalproject.dao.repository.SellerFollowRepository;
import org.example.trendyolfinalproject.dao.repository.SellerRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SellerFollowResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.example.trendyolfinalproject.service.SellerFollowService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerFollowServiceImpl implements SellerFollowService {

    private final SellerFollowRepository sellerFollowRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final SellerRepository sellerRepository;


    @Override
    public ApiResponse<String> follow(Long sellerId) {
        log.info("Actionlog.follow.start : sellerId={}", sellerId);
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var seller = sellerRepository.findById(sellerId).orElseThrow(() -> new NotFoundException("Seller not found with id: " + sellerId));

        var follow = SellerFollow.builder()
                .followedAt(java.time.LocalDateTime.now())
                .follower(user)
                .seller(seller)
                .build();
        sellerFollowRepository.save(follow);
        auditLogService.createAuditLog(user, "follow", "followed seller");
        notificationService.sendNotification(user, "You have new follower " + user.getName(), NotificationType.NEW_FOLLOWER, user.getId());

        log.info("Actionlog.follow.end : sellerId={}", sellerId);
        return ApiResponse.success("You have followed " + seller.getUser().getName());
    }


    @Override
    public ApiResponse<List<SellerFollowResponse>> getAllFollowers() {
        log.info("Actionlog.getAllFollowers.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var seller = sellerRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("Seller not found with id: " + userId));
        var followers = sellerFollowRepository.findBySeller(seller);

        var response = followers.stream()
                .map(f -> SellerFollowResponse.builder()
                        .seller(f.getSeller().getUser().getName())
                        .followedAt(f.getFollowedAt())
                        .follower(f.getFollower().getName())
                        .build())
                .toList();


        log.info("Actionlog.getAllFollowers.end : ");
        return ApiResponse.<List<SellerFollowResponse>>builder()
                .data(response)
                .message("All followers fetched successfully")
                .status(200)
                .build();
    }

    @Override
    public ApiResponse<String> unfollow(Long sellerId) {
        log.info("Actionlog.unfollow.start : sellerId={}", sellerId);
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var seller = sellerRepository.findById(sellerId).orElseThrow(() -> new NotFoundException("Seller not found with id: " + sellerId));

        var follow = sellerFollowRepository.findBySellerAndFollower(seller, user).orElseThrow(() -> new NotFoundException("You are not following this seller"));
        sellerFollowRepository.delete(follow);
        auditLogService.createAuditLog(user, "unfollow", "unfollowed seller");
        notificationService.sendNotification(seller.getUser(), "Follower " + user.getName() + " has unfollowed you", NotificationType.UNFOLLOWER, user.getId());
        log.info("Actionlog.unfollow.end : sellerId={}", sellerId);
        return ApiResponse.success("You have unfollowed " + seller.getUser().getName());
    }


    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }


}
