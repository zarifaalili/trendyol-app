package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Notification;
import org.example.trendyolfinalproject.request.NotificationCreateRequest;
import org.example.trendyolfinalproject.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "readStatus", expression = "java(org.example.trendyolfinalproject.model.ReadStatus.UNREAD)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "sentAt", expression = "java(java.time.LocalDateTime.now())")
    Notification toEntity(NotificationCreateRequest request);

    @Mapping(source = "user.id", target = "userId")
    NotificationResponse toResponse(Notification notification);


    List<NotificationResponse> toResponseList(List<Notification> notifications);
}
