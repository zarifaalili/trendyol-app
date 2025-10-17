package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.NotificationResponse;
import org.example.trendyolfinalproject.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotification() {
        return ResponseEntity.ok().body(notificationService.getAllNotificationsByUserId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> readSingleNotification(@PathVariable Long id) {
        return ResponseEntity.ok().body(notificationService.readSingleNotification(id));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> readUnreadNotification() {
        return ResponseEntity.ok().body(notificationService.getUnreadNotifications());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> searchNotification(@RequestParam String keyword) {
        return ResponseEntity.ok().body(notificationService.searchNotification(keyword));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        return ResponseEntity.ok().body(notificationService.markAllAsRead());
    }


    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadNotificationCount() {
        return ResponseEntity.ok().body(notificationService.getUnreadNotificationCount());
    }

}
