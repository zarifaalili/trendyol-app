package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.JoinRequest;
import org.example.trendyolfinalproject.model.response.JoinRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JoinRequestMapper {

    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.title", target = "groupName")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "requester.email", target = "requesterEmail")
    JoinRequestResponse toResponse(JoinRequest joinRequest);

    List<JoinRequestResponse> toResponseList(List<JoinRequest> joinRequests);


}
