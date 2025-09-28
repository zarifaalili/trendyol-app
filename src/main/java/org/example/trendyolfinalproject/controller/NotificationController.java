package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.NotificationResponse;
import org.example.trendyolfinalproject.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotification() {
        return notificationService.getAllNotificationsByUserId();
    }

    @GetMapping("/{id}")
    public ApiResponse<NotificationResponse> readSingleNotification(@PathVariable Long id) {
        return notificationService.readSingleNotification(id);
    }

    @PostMapping("/unread")
    public ApiResponse<List<NotificationResponse>> readUnreadNotification() {
        return notificationService.getUnreadNotifications();
    }

    @GetMapping("/search")
    public ApiResponse<List<NotificationResponse>> searchNotification(@RequestParam String keyword) {
        return notificationService.searchNotification(keyword);
    }

    @PatchMapping("/mark-all-read")
    public ApiResponse<String> markAllAsRead() {
        return notificationService.markAllAsRead();
    }


    @GetMapping("/unread/count")
    public ApiResponse<Integer> getUnreadNotificationCount() {
        return notificationService.getUnreadNotificationCount();
    }

}
