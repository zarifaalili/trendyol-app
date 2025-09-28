package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.*;
import org.example.trendyolfinalproject.model.request.ChatMessageRequest;
import org.example.trendyolfinalproject.model.response.AttachmentResponse;
import org.example.trendyolfinalproject.model.response.ChatMessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sender", source = "sender")
    @Mapping(target = "group", source = "group")
    @Mapping(target = "productVariant", source = "productVariant")
    GroupMessage toEntity(ChatMessageRequest request, User sender, ChatGroup group, ProductVariant productVariant);

    @Mapping(target = "senderId", expression = "java(message.getSender().getId())")
    @Mapping(target = "groupId", expression = "java(message.getGroup().getId())")
    @Mapping(target = "productVariantId", expression = "java(message.getProductVariant() != null ? message.getProductVariant().getId() : null)")
    @Mapping(target = "deletedForAll", expression = "java(message.getDeletedForAll())")
    @Mapping(target = "edited", expression = "java(message.getEdited())")
    @Mapping(target = "text", source = "decryptedMessage")
    ChatMessageResponse toResponse(GroupMessage message,String decryptedMessage);


    default List<ChatMessageResponse> toResponseList(List<GroupMessageIndex> indexes) {
        return indexes.stream().map(index -> {
            ChatMessageResponse response = new ChatMessageResponse();
            try {
                response.setId(Long.parseLong(index.getId()));
            } catch(NumberFormatException e) {
                response.setId(null);
            }
            response.setSenderId(index.getSenderId());
            response.setGroupId(index.getGroupId());
            response.setText(index.getMessage());
            response.setSentAt(index.getTimestamp() != null
                    ? LocalDateTime.ofInstant(index.getTimestamp(), ZoneOffset.UTC)
                    : null);
            response.setType(null);
            response.setForwardedFromMessageId(null);
            response.setProductVariantId(null);
            response.setEdited(false);
            response.setDeletedForAll(false);
            response.setAttachments(null);
            return response;
        }).collect(Collectors.toList());
    }

}
