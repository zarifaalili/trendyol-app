package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.enums.DeliveryChannelType;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.ReadStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private ReadStatus readStatus;
    private DeliveryChannelType deliveryChannelType;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private Integer relatedEntityId;
}
