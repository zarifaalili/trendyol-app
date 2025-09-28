package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Coupon;
import org.example.trendyolfinalproject.model.request.CouponCreateRequest;
import org.example.trendyolfinalproject.model.response.CouponResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "usageLimit", source = "usageLimit")
    @Mapping(target = "perUserLimit", source = "perUserLimit")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "discountType", source = "discountType")
    @Mapping(target = "discountValue", source = "discountValue")
    @Mapping(target = "minimumOrderAmount", source = "minimumOrderAmount")
    @Mapping(target = "maximumDiscountAmount", source = "maximumDiscountAmount")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target="firstOrderOnly", source = "firstOrderOnly")
    Coupon toEntity(CouponCreateRequest request);


    CouponResponse toResponse(Coupon coupon);

    List<CouponResponse> toResponseList(List<Coupon> coupons);


}
