package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Notification;
import org.example.trendyolfinalproject.model.ReadStatus;
import org.example.trendyolfinalproject.model.enums.DeliveryChannelType;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.request.NotificationCreateRequest;
import org.example.trendyolfinalproject.model.response.NotificationResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {

    private final NotificationMapper mapper = Mappers.getMapper(NotificationMapper.class);

    @Test
    void testToEntity() {
        NotificationCreateRequest request = new NotificationCreateRequest(
                1L,
                NotificationType.SYSTEM,
                "Welcome!",
                DeliveryChannelType.EMAIL,
                100
        );

        Notification notification = mapper.toEntity(request);

        assertNotNull(notification);
        assertNull(notification.getId());
        assertNull(notification.getUser());
        assertEquals(ReadStatus.UNREAD, notification.getReadStatus());
        assertEquals("Welcome!", notification.getMessage());
        assertEquals(NotificationType.SYSTEM, notification.getType());
        assertEquals(DeliveryChannelType.EMAIL, notification.getDeliveryChannelType());
        assertEquals(100L, notification.getRelatedEntityId());
        assertNotNull(notification.getCreatedAt());
        assertNotNull(notification.getSentAt());
    }

    @Test
    void testToResponse() {
        Notification notification = new Notification();
        notification.setId(10L);
        notification.setMessage("Test message");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setType(NotificationType.SYSTEM);
        notification.setDeliveryChannelType(DeliveryChannelType.SMS);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSentAt(LocalDateTime.now());
        notification.setRelatedEntityId(77L);

        var user = new org.example.trendyolfinalproject.dao.entity.User();
        user.setId(5L);
        notification.setUser(user);

        NotificationResponse response = mapper.toResponse(notification);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(5L, response.getUserId());
        assertEquals("Test message", response.getMessage());
        assertEquals(ReadStatus.UNREAD, response.getReadStatus());
        assertEquals(NotificationType.SYSTEM, response.getType());
        assertEquals(DeliveryChannelType.SMS, response.getDeliveryChannelType());
    }
}
