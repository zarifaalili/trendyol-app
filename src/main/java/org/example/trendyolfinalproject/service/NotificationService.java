package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Notification;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.NotificationRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.NotificationMapper;
import org.example.trendyolfinalproject.model.enums.DeliveryChannelType;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.ReadStatus;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.NotificationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationMapper notificationMapper;
    private final AuditLogService auditLogService;

    public void sendToAllUsers(String message, NotificationType type, Long relatedEntityId) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            sendNotification(user, message, type, relatedEntityId);

        }

    }

    public void sendNotification(User user, String message, NotificationType type, Long relatedId) {

        for (DeliveryChannelType deliveryChannelType : DeliveryChannelType.values()) {


            switch (deliveryChannelType) {
                case IN_APP, PUSH_NOTIFICATION, SMS -> {
                    Notification notification = new Notification();
                    notification.setUser(user);
                    notification.setMessage(message);
                    notification.setType(type);
                    notification.setDeliveryChannelType(deliveryChannelType);
                    notification.setRelatedEntityId(relatedId);
                    notification.setSentAt(LocalDateTime.now());
                    notification.setReadStatus(ReadStatus.UNREAD);
                    notification.setCreatedAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                }

                case EMAIL -> {
                    emailService.sendEmail(user.getEmail(), "Trendyol", message);
                }


//            if (deliveryChannelType == DeliveryChannelType.IN_APP) {
//                Notification notification = new Notification();
//                notification.setUser(user);
//                notification.setMessage(message);
//                notification.setType(type);
//                notification.setDeliveryChannelType(deliveryChannelType);
//                notification.setRelatedEntityId(relatedId);
//                notificationRepository.save(notification);
//            }
//
//
//            if (deliveryChannelType == DeliveryChannelType.EMAIL) {
//                emailService.sendEmail(user.getEmail(), "Trendyol", message);
//            }
//
//
//            if (deliveryChannelType == DeliveryChannelType.SMS) {
//                Notification notification = new Notification();
//                notification.setUser(user);
//                notification.setMessage(message);
//                notification.setType(type);
//                notification.setDeliveryChannelType(deliveryChannelType);
//                notification.setRelatedEntityId(relatedId);
//                notificationRepository.save(notification);
//            }
//
//            if(deliveryChannelType==DeliveryChannelType.PUSH_NOTIFICATION){
//                Notification notification = new Notification();
//                notification.setUser(user);
//                notification.setMessage(message);
//                notification.setType(type);
//                notification.setDeliveryChannelType(deliveryChannelType);
//                notification.setRelatedEntityId(relatedId);
//                notificationRepository.save(notification);
//            }


            }

        }
    }

    public void sendToUsersWithWishListVariant(String message, NotificationType type, Long variantId) {
        List<User> users = userRepository.findAllByProductVariantIdInWishList(variantId);

        for (User user : users) {
            sendNotification(user, message, type, variantId);
        }
    }

    public void sendToUsersWithBasketVariant(String message, NotificationType type, Long variantId) {
        List<User> users = userRepository.findAllByProductVariantIdInBasket(variantId);

        for (User user : users) {
            sendNotification(user, message, type, variantId);
        }
    }


    public void sendToAdmins(String message, NotificationType type, Long relatedEntityId) {
        List<User> admins = userRepository.findAllAdmins();
        for (User admin : admins) {
            sendNotification(admin, message, type, relatedEntityId);
        }
    }

    public void sendToAllCustomers(String message, NotificationType type, Long relatedEntityId) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getRole().name().equals("CUSTOMER"))
                .toList();

        for (User user : users) {
            sendNotification(user, message, type, relatedEntityId);
        }
    }


    public ApiResponse<List<NotificationResponse>> getAllNotificationsByUserId() {
        Long userId = getCurrentUserId();

        log.info("Actionlog.getNotification.start : userId={}", userId);
        var notification = notificationRepository.findByUserId(userId);

        if (notification.isEmpty()) {
            throw new NotFoundException("Notification not found");
        }
        var response = notificationMapper.toResponseList(notification);
        log.info("Actionlog.getNotification.end : userId={}", userId);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(), "GET ALL NOTIFICATION", "Notification get successfully.");
        return ApiResponse.<List<NotificationResponse>>builder()
                .status(200)
                .message("Notifications retrieved successfully")
                .data(response)
                .build();
    }

    public ApiResponse<NotificationResponse> readSingleNotification(Long id) {
        Long userId = getCurrentUserId();
        log.info("Actionlog.getSingleNotification.start : userId={}", userId);
        var notification = notificationRepository.findByUserIdAndIdAndDeliveryChannelType(userId, id, DeliveryChannelType.IN_APP).orElseThrow(
                () -> new NotFoundException("Notification not found with userId: " + userId + " id: " + id)
        );

        if (!notification.getReadStatus().equals(ReadStatus.READ)) {
            notification.setReadStatus(ReadStatus.READ);
        }

        notificationRepository.save(notification);
        var response = notificationMapper.toResponse(notification);
        log.info("Actionlog.getSingleNotification.end : userId={}", userId);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(), "GET SINGLE NOTIFICATION", "Notification get successfully.");
        return ApiResponse.<NotificationResponse>builder()
                .status(200)
                .message("Notification marked as read and retrieved successfully")
                .data(response)
                .build();
    }

    public ApiResponse<List<NotificationResponse>> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        log.info("Actionlog.getUnreadNotification.start : userId={}", userId);
        var notification = notificationRepository.findByUserIdAndReadStatus(userId, ReadStatus.UNREAD);
        if (notification.isEmpty()) {
            throw new RuntimeException("Notification not found");
        }
        var response = notificationMapper.toResponseList(notification);
        log.info("Actionlog.getUnreadNotification.end : userId={}", userId);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(), "GET UNREAD NOTIFICATIONS", "Notification get successfully.");
        return ApiResponse.<List<NotificationResponse>>builder()
                .status(200)
                .message("Unread notifications retrieved successfully")
                .data(response)
                .build();
    }


    public ApiResponse<List<NotificationResponse>> searchNotification(String message) {
        Long userId = getCurrentUserId();
        log.info("Actionlog.searchNotification.start : userId={}", userId);
        var notification = notificationRepository.search(message);
        if (notification.isEmpty()) {
            return ApiResponse.<List<NotificationResponse>>builder()
                    .status(404)
                    .message("No notifications found matching the search criteria")
                    .data(List.of())
                    .build();
        }
        var response = notificationMapper.toResponseList(notification);
        log.info("Actionlog.searchNotification.end : userId={}", userId);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(), "SEARCH NOTIFICATION", "Notification get successfully.");
        return ApiResponse.<List<NotificationResponse>>builder()
                .status(200)
                .message("Notifications retrieved successfully")
                .data(response)
                .build();
    }

    public ApiResponse<String> markAllAsRead() {
        var userId = getCurrentUserId();
        log.info("Actionlog.markAllNotificationAsRead.start : userId={}", userId);
        var unreadNnotification = notificationRepository.findByReadStatus(ReadStatus.UNREAD);

        if (unreadNnotification.isEmpty()) {
            return ApiResponse.<String>builder()
                    .status(200)
                    .message("Notifications already marked as read")
                    .data(null)
                    .build();        }
        unreadNnotification.forEach(notification -> notification.setReadStatus(ReadStatus.READ));
        notificationRepository.saveAll(unreadNnotification);

        log.info("Actionlog.markAllNotificationAsRead.end : userId={}", userId);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(), "Change all notification as read", "MARK ALL NOTIFICATION AS READ");
        return ApiResponse.<String>builder()
                .status(200)
                .message("All notifications marked as read")
                .data(null)
                .build();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }


    public  ApiResponse<Integer> getUnreadNotificationCount() {
        Long userId = getCurrentUserId();
        log.info("Actionlog.getUnreadNotificationCount.start : userId={}", userId);
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var notification = notificationRepository.findByUserIdAndReadStatus(userId, ReadStatus.UNREAD);
        var count = notification.size();
        log.info("Actionlog.getUnreadNotificationCount.end : userId={}", userId);
        auditLogService.createAuditLog(user, "GET UNREAD NOTIFICATION COUNT", "Notification get successfully.");
        return ApiResponse.<Integer>builder()
                .status(200)
                .message("Unread notification count retrieved successfully")
                .data(count)
                .build();    }


}
