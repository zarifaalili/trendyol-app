package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.enums.DeliveryChannelType;
import org.example.trendyolfinalproject.model.enums.NotificationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateRequest {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Notification type cannot be null")
    private NotificationType type;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotNull(message = "Delivery channel type cannot be null")
    private DeliveryChannelType deliveryChannelType;

    private Integer relatedEntityId;

}
