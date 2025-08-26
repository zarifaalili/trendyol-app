//package org.example.trendyolfinalproject.mapper;
//
//import org.example.trendyolfinalproject.dao.entity.UserCoupun;
//import org.example.trendyolfinalproject.request.UserCouponCreateRequest;
//import org.example.trendyolfinalproject.response.UserCouponResponse;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//
//@Mapper(componentModel = "spring")
//public interface UserCoupunMapper {
//
//    UserCoupunMapper INSTANCE = Mappers.getMapper(UserCoupunMapper.class);
//
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "user", ignore = true)
//    @Mapping(target = "coupun", ignore = true)
//    UserCoupun toEntity(UserCouponCreateRequest request);
//
//
//
//    @Mapping(source = "user.id", target = "userId")
//    @Mapping(source = "coupun.id", target = "couponId")
//    @Mapping(source = "coupun.code", target = "couponCode")
////    @Mapping(source = "coupun.name", target = "couponName")
//    UserCouponResponse toResponse(UserCoupun userCoupun);
//
//
//
//
//
//}
