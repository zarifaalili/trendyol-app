package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.Review;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.request.ReviewCreateRequest;
import org.example.trendyolfinalproject.model.request.ReviewUpdateRequest;
import org.example.trendyolfinalproject.model.response.ReviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewMapperTest {

    private ReviewMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ReviewMapper.class);
    }

    @Test
    void testToEntity() {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setProductId(100L);
        request.setRating(5);
        request.setComment("Excellent product!");

        Review entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getRating(), entity.getRating());
        assertEquals(request.getComment(), entity.getComment());
        assertFalse(entity.getIsApproved());
        assertNotNull(entity.getReviewDate());
        assertNull(entity.getUser());
        assertNull(entity.getProduct());
    }

    @Test
    void testUpdateEntity() {
        Review review = new Review();
        review.setRating(3);
        review.setComment("Old comment");

        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest();
        updateRequest.setRating(4);
        updateRequest.setComment("Updated comment");

        mapper.updateEntity(review, updateRequest);

        assertEquals(4, review.getRating());
        assertEquals("Updated comment", review.getComment());
    }

    @Test
    void testToResponseMapping() {
        User user = new User();
        user.setId(1L);
        user.setName("Zari");
        user.setSurname("Aliyeva");

        Product product = new Product();
        product.setId(2L);
        product.setName("Laptop");

        Review review = new Review();
        review.setId(10L);
        review.setUser(user);
        review.setProduct(product);
        review.setRating(5);
        review.setComment("Great product!");
        review.setReviewDate(LocalDateTime.now().toString());
        review.setIsApproved(true);

        ReviewResponse response = mapper.toResponse(review);

        assertNotNull(response);
        assertEquals(review.getId(), response.getId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals("Zari Aliyeva", response.getUserName());
        assertEquals(product.getId(), response.getProductId());
        assertEquals(product.getName(), response.getProductName());
        assertEquals(review.getRating(), response.getRating());
        assertEquals(review.getComment(), response.getComment());
        assertEquals(review.getIsApproved(), response.getIsApproved());
        assertNotNull(response.getReviewDate());
    }

    @Test
    void testToResponseList() {
        User user = new User();
        user.setId(1L);
        user.setName("Zari");
        user.setSurname("Aliyeva");

        Product product = new Product();
        product.setId(2L);
        product.setName("Laptop");

        Review review1 = new Review();
        review1.setId(10L);
        review1.setUser(user);
        review1.setProduct(product);
        review1.setRating(5);
        review1.setComment("Great product!");
        review1.setReviewDate(LocalDateTime.now().toString());
        review1.setIsApproved(true);

        Review review2 = new Review();
        review2.setId(11L);
        review2.setUser(user);
        review2.setProduct(product);
        review2.setRating(4);
        review2.setComment("Good product!");
        review2.setReviewDate(LocalDateTime.now().toString());
        review2.setIsApproved(false);

        List<Review> reviews = Arrays.asList(review1, review2);
        List<ReviewResponse> responses = mapper.toResponseList(reviews);

        assertEquals(2, responses.size());
        assertEquals("Great product!", responses.get(0).getComment());
        assertEquals("Good product!", responses.get(1).getComment());
    }
}
