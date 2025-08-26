package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
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
    public List<NotificationResponse> getNotification() {
        return notificationService.getAllNotificationsByUserId();
    }

    @GetMapping("/readSingleNotification/{id}")
    public NotificationResponse readSingleNotification(@PathVariable Long id) {
        return notificationService.readSingleNotification(id);
    }

    @PostMapping("/readUnreadNotification")
    public List<NotificationResponse> readUnreadNotification() {
        return notificationService.getUnreadNotifications();
    }

    @GetMapping("/searchNotification")
    public List<NotificationResponse> searchNotification(@RequestParam String keyword) {
        return notificationService.searchNotification(keyword);
    }

    @PatchMapping("/markAllAsRead")
    public String markAllAsRead() {
        return notificationService.markAllAsRead();
    }


    @GetMapping("/unread/count")
    public Integer getUnreadNotificationCount() {
        return notificationService.getUnreadNotificationCount();
    }

}
