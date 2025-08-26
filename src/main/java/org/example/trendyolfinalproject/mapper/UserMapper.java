package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.request.UserRegisterRequest;
import org.example.trendyolfinalproject.response.UserProfileResponse;
import org.example.trendyolfinalproject.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "isActive", constant = "true")
    User toEntity(UserRegisterRequest request);

//    @Mapping(target = "phoneNumber", expression = "java(formatPhoneNumber(request.getPhoneNumber()))")
//    User toEntityFromRequest(UserRequest request);


    @Mapping(target = "fullName", expression = "java(user.getName() + \" \" + user.getSurname())")
    @Mapping(target = "username", source = "email")
    @Mapping(target = "addresses", ignore = true)           // Service-də ayrıca set olunacaq
    @Mapping(target = "defaultPaymentMethod", ignore = true) // Service-də ayrıca set olunacaq
    @Mapping(target = "wishlistCount", ignore = true)
    @Mapping(target = "orderCount", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    UserProfileResponse toUserProfileResponse(User user);

    UserResponse toUserResponse(User user);

//    List<UserResponse> ToUserResponseList(List<User> users);

    List<UserResponse> toResponseList(List<User> users);
//    default String formatPhoneNumber(String phoneNumber) {
//        if (Objects.isNull(phoneNumber)) return null;
//        if (phoneNumber.startsWith("+994")) return phoneNumber;
//        return "+994" + phoneNumber;
//    }

}
