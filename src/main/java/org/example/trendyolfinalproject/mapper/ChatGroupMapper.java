package org.example.trendyolfinalproject.mapper;

import jdk.dynalink.linker.LinkerServices;
import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.model.request.ChatGroupRequest;
import org.example.trendyolfinalproject.model.response.ChatGroupResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatGroupMapper {

    @Mapping(source="owner.id", target = "ownerId")
    ChatGroupResponse toResponse(ChatGroup chatGroup);

    ChatGroup toEntity(ChatGroupRequest chatGroupRequest);

    List<ChatGroupResponse> toResponseList(List<ChatGroup> chatGroups);
}
