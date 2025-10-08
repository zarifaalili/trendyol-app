package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    void sendToAllUsers(String message, NotificationType type, Long relatedEntityId);

    void sendNotification(User user, String message, NotificationType type, Long relatedId);

    void sendToUsersWithWishListVariant(String message, NotificationType type, Long variantId);

    void sendToUsersWithBasketVariant(String message, NotificationType type, Long variantId);

    void sendToAdmins(String message, NotificationType type, Long relatedEntityId);

    void sendToAllCustomers(String message, NotificationType type, Long relatedEntityId);

    ApiResponse<List<NotificationResponse>> getAllNotificationsByUserId();

    ApiResponse<NotificationResponse> readSingleNotification(Long id);

    ApiResponse<List<NotificationResponse>> getUnreadNotifications();

    ApiResponse<List<NotificationResponse>> searchNotification(String message);

    ApiResponse<String> markAllAsRead();

    ApiResponse<Integer> getUnreadNotificationCount();

}
