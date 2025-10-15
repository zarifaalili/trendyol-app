package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Review;
import org.example.trendyolfinalproject.dao.entity.ReviewImage;
import org.example.trendyolfinalproject.model.request.ReviewImageCreateRequest;
import org.example.trendyolfinalproject.model.response.ReviewImageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewImageMapperTest {

    private ReviewImageMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ReviewImageMapper.class);
    }

    @Test
    void testToEntityMapping() {
        ReviewImageCreateRequest request = new ReviewImageCreateRequest();
        request.setReviewId(5L);
        request.setImageUrl("http://example.com/image.jpg");

        Review review = new Review();
        review.setId(request.getReviewId());

        ReviewImage entity = mapper.toEntity(request);
        entity.setReview(review);

        assertNotNull(entity);
        assertEquals(request.getImageUrl(), entity.getImageUrl());
        assertNotNull(entity.getCreatedAt());
        assertEquals(review.getId(), entity.getReview().getId());
    }

    @Test
    void testToResponseMapping() {
        Review review = new Review();
        review.setId(5L);

        ReviewImage reviewImage = new ReviewImage();
        reviewImage.setId(10L);
        reviewImage.setReview(review);
        reviewImage.setImageUrl("http://example.com/image.jpg");
        reviewImage.setCreatedAt(LocalDateTime.now());

        ReviewImageResponse response = mapper.toResponse(reviewImage);

        assertNotNull(response);
        assertEquals(reviewImage.getId(), response.getId());
        assertEquals(review.getId(), response.getReviewId());
        assertEquals(reviewImage.getImageUrl(), response.getImageUrl());
        assertEquals(reviewImage.getCreatedAt(), response.getCreatedAt());
    }
}
