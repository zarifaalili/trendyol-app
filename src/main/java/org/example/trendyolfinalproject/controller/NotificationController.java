package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.example.trendyolfinalproject.response.NotificationResponse;
import org.example.trendyolfinalproject.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/getNotificationsByUserId")
    public ApiResponse<List<NotificationResponse>> getNotification() {
        return notificationService.getAllNotificationsByUserId();
    }

    @GetMapping("/readSingleNotification/{id}")
    public ApiResponse<NotificationResponse> readSingleNotification(@PathVariable Long id) {
        return notificationService.readSingleNotification(id);
    }

    @PostMapping("/readUnreadNotification")
    public ApiResponse<List<NotificationResponse>> readUnreadNotification() {
        return notificationService.getUnreadNotifications();
    }

    @GetMapping("/searchNotification")
    public ApiResponse<List<NotificationResponse>> searchNotification(@RequestParam String keyword) {
        return notificationService.searchNotification(keyword);
    }

    @PatchMapping("/markAllAsRead")
    public ApiResponse<String> markAllAsRead() {
        return notificationService.markAllAsRead();
    }


    @GetMapping("/unread/count")
    public ApiResponse<Integer> getUnreadNotificationCount() {
        return notificationService.getUnreadNotificationCount();
    }

}
