package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.UserWheel;
import org.example.trendyolfinalproject.dao.entity.Wheel;
import org.example.trendyolfinalproject.dao.entity.WheelPrize;
import org.example.trendyolfinalproject.model.request.WheelPrizeRequest;
import org.example.trendyolfinalproject.model.request.WheelRequest;
import org.example.trendyolfinalproject.model.response.SpinWheelResponse;
import org.example.trendyolfinalproject.model.response.WheelPrizeResponse;
import org.example.trendyolfinalproject.model.response.WheelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WheelMapper {


    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "minOrder", source = "minOrder")
    WheelPrizeResponse toResponse(WheelPrize prize);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "wheel", ignore = true)
    WheelPrize toEntity(WheelPrizeRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    @Mapping(target = "prizes", source = "prizes")
    WheelResponse toResponse(Wheel wheel);

    List<WheelResponse> toResponseList(List<Wheel> wheels);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prizes", ignore = true)
    @Mapping(target = "startTime", ignore = true)
    Wheel toEntity(WheelRequest request);

    @Mapping(target = "wheelId", source = "wheel.id")
    @Mapping(target = "wheelName", source = "wheel.name")
    @Mapping(target = "prizeId", source = "prize.id")
    @Mapping(target = "prizeName", source = "prize.name")
    @Mapping(target = "amount", source = "prize.amount")
    @Mapping(target = "minOrder", source = "prize.minOrder")
    @Mapping(target = "expiresAt", source = "expiresAt")
    SpinWheelResponse toResponse(UserWheel userWheel);
}
