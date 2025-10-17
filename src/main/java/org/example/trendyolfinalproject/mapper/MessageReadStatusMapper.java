package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.MessageReadStatus;
import org.example.trendyolfinalproject.model.response.MessageReadStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageReadStatusMapper {

    @Mapping(target = "messageId", source = "message.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.name")
    MessageReadStatusResponse toResponse(MessageReadStatus readStatus);

    List<MessageReadStatusResponse> toResponseList(List<MessageReadStatus> readStatuses);
}
