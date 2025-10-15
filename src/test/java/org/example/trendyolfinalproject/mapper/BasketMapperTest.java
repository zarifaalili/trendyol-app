package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Basket;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.request.BasketCreateRequest;
import org.example.trendyolfinalproject.model.response.BasketResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasketMapperTest {

    private BasketMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BasketMapper.class);
    }

    @Test
    void testToEntity() {
        // given
        BasketCreateRequest request = new BasketCreateRequest(10L);

        // when
        Basket basket = mapper.toEntity(request);

        // then
        assertNotNull(basket);
        assertNull(basket.getId());
        assertNull(basket.getUser());
        assertNotNull(basket.getCreatedAt());
        assertNotNull(basket.getUpdatedAt());
        assertTrue(basket.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(basket.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testToResponse() {
        // given
        User user = new User();
        user.setId(7L);

        Basket basket = new Basket();
        basket.setId(5L);
        basket.setUser(user);
        basket.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0));
        basket.setUpdatedAt(LocalDateTime.of(2025, 1, 2, 12, 0));
        basket.setDiscountAmount(BigDecimal.valueOf(10));
        basket.setFinalAmount(BigDecimal.valueOf(90));

        // when
        BasketResponse response = mapper.toResponse(basket);

        // then
        assertEquals(5L, response.getId());
        assertEquals(7L, response.getUserId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 0), response.getCreatedAt());
        assertEquals(LocalDateTime.of(2025, 1, 2, 12, 0), response.getUpdatedAt());
        assertEquals(BigDecimal.valueOf(10), response.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(90), response.getFinalAmount());
    }

    @Test
    void testToResponseList() {
        // given
        User user = new User();
        user.setId(1L);

        Basket basket = new Basket();
        basket.setId(100L);
        basket.setUser(user);

        List<BasketResponse> list = mapper.toResponseList(List.of(basket));

        // then
        assertEquals(1, list.size());
        assertEquals(100L, list.get(0).getId());
        assertEquals(1L, list.get(0).getUserId());
    }
}
