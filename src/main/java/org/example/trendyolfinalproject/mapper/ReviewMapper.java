package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Review;
import org.example.trendyolfinalproject.dao.entity.ReviewImage;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.projection.NegativeReviewProjection;
import org.example.trendyolfinalproject.projection.TopProductProjection;
import org.example.trendyolfinalproject.model.request.ReviewCreateRequest;
import org.example.trendyolfinalproject.model.request.ReviewUpdateRequest;
import org.example.trendyolfinalproject.model.response.ReviewResponse;
import org.example.trendyolfinalproject.model.response.TopRatedProductResponse;
import org.mapstruct.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "reviewDate", expression = "java(java.time.LocalDateTime.now().toString())")
    @Mapping(target = "isApproved", constant = "false")
    Review toEntity(ReviewCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    void updateEntity(@MappingTarget Review review, ReviewUpdateRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userName", qualifiedByName = "mapUserToFullName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "reviewDate", target = "reviewDate", qualifiedByName = "convertStringToLocalDateTime")
    @Mapping(target = "imageUrls", ignore = true) // bunu sonradan manual set edəcəyik
    ReviewResponse toResponse(Review review);

    List<ReviewResponse> toResponseList(List<Review> reviews);



    List<TopRatedProductResponse> toResponseListProjection(List<TopProductProjection> list);

    List<NegativeReviewProjection> toNegativeReviewProjectionList(List<NegativeReviewProjection> list);

            @Named("mapImageUrls")
    default List<String> mapImageUrls(List<ReviewImage> images) {
        if (images == null) {
            return null;
        }
        return images.stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());
    }

    @Named("convertStringToLocalDateTime")
    default LocalDateTime convertStringToLocalDateTime(String reviewDateString) {
        if (reviewDateString == null || reviewDateString.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(reviewDateString);
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("Error parsing reviewDate: " + reviewDateString + " - " + e.getMessage());
            return null;
        }
    }

    @Named("mapUserToFullName")
    default String mapUserToFullName(User user) {
        if (user == null) {
            return null;
        }

        String name = Optional.ofNullable(user.getName()).orElse("");
        String surname = Optional.ofNullable(user.getSurname()).orElse("");

        if (name.isEmpty() && surname.isEmpty()) {
            return null;
        }
        return (name + " " + surname).trim();
    }
}
