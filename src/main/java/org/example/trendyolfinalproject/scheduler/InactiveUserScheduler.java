package org.example.trendyolfinalproject.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.AuditLog;
import org.example.trendyolfinalproject.dao.entity.Notification;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.AuditLogRepository;
import org.example.trendyolfinalproject.dao.repository.NotificationRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.model.enums.DeliveryChannelType;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.ReadStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InactiveUserScheduler {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 20 * * *")
    public void notifyInactiveUsers() {
        log.info("Inactive user scheduler started...");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            AuditLog lastLog = auditLogRepository.findLastByUser(user).orElse(null);

            boolean activeToday = lastLog != null && lastLog.getCreatedAt().toLocalDate().isEqual(LocalDate.now());

            if (!activeToday) {
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setType(NotificationType.ALERT);
                notification.setMessage("Siz bu gün Trendyola daxil olmamısız 🚨");
                notification.setReadStatus(ReadStatus.UNREAD);
                notification.setDeliveryChannelType(DeliveryChannelType.IN_APP);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setSentAt(LocalDateTime.now());

                notificationRepository.save(notification);

                log.info("Notification sent to inactive user: {}", user.getEmail());
            }
        }
    }
}

