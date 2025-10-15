package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Coupon;
import org.example.trendyolfinalproject.model.enums.DiscountType;
import org.example.trendyolfinalproject.model.request.CouponCreateRequest;
import org.example.trendyolfinalproject.model.response.CouponResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CouponMapperTest {

    private CouponMapper couponMapper;

    @BeforeEach
    void setUp() {
        couponMapper = Mappers.getMapper(CouponMapper.class);
    }

    @Test
    void testToEntity() {
        // given
        CouponCreateRequest request = new CouponCreateRequest(
                "DISCOUNT50",
                DiscountType.PERCENTAGE,
                new BigDecimal("50"),
                new BigDecimal("100"),
                new BigDecimal("500"),
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                100,
                1,
                true,
                true,
                1
        );

        // when
        Coupon coupon = couponMapper.toEntity(request);

        // then
        assertNotNull(coupon);
        assertNull(coupon.getId()); // ignore=true
        assertEquals("DISCOUNT50", coupon.getCode());
        assertEquals(DiscountType.PERCENTAGE, coupon.getDiscountType());
        assertEquals(new BigDecimal("50"), coupon.getDiscountValue());
        assertEquals(new BigDecimal("100"), coupon.getMinimumOrderAmount());
        assertEquals(new BigDecimal("500"), coupon.getMaximumDiscountAmount());
        assertEquals(request.getStartDate(), coupon.getStartDate());
        assertEquals(request.getEndDate(), coupon.getEndDate());
        assertEquals(100, coupon.getUsageLimit());
        assertEquals(1, coupon.getPerUserLimit());
        assertTrue(coupon.getIsActive());
        assertTrue(coupon.getFirstOrderOnly());
        assertEquals(1, coupon.getMinOrderCount());
        assertNotNull(coupon.getCreatedAt());
        assertNotNull(coupon.getUpdatedAt());
    }

    @Test
    void testToResponse() {
        // given
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("DISCOUNT50");
        coupon.setDiscountType(DiscountType.PERCENTAGE);
        coupon.setDiscountValue(new BigDecimal("50"));
        coupon.setMinimumOrderAmount(new BigDecimal("100"));
        coupon.setMaximumDiscountAmount(new BigDecimal("500"));
        coupon.setStartDate(LocalDate.now().plusDays(1));
        coupon.setEndDate(LocalDateTime.now().plusDays(10));
        coupon.setUsageLimit(100);
        coupon.setPerUserLimit(1);
        coupon.setIsActive(true);
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUpdatedAt(LocalDateTime.now());

        // when
        CouponResponse response = couponMapper.toResponse(coupon);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("DISCOUNT50", response.getCode());
        assertEquals(DiscountType.PERCENTAGE, response.getDiscountType());
        assertEquals(new BigDecimal("50"), response.getDiscountValue());
        assertEquals(new BigDecimal("100"), response.getMinimumOrderAmount());
        assertEquals(new BigDecimal("500"), response.getMaximumDiscountAmount());
        assertEquals(coupon.getStartDate(), response.getStartDate());
        assertEquals(coupon.getEndDate(), response.getEndDate());
        assertEquals(100, response.getUsageLimit());
        assertEquals(1, response.getPerUserLimit());
        assertTrue(response.getIsActive());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void testToResponseList() {
        // given
        Coupon coupon1 = new Coupon();
        coupon1.setId(1L);
        coupon1.setCode("C1");

        Coupon coupon2 = new Coupon();
        coupon2.setId(2L);
        coupon2.setCode("C2");

        List<Coupon> coupons = List.of(coupon1, coupon2);

        // when
        List<CouponResponse> responses = couponMapper.toResponseList(coupons);

        // then
        assertEquals(2, responses.size());
        assertEquals("C1", responses.get(0).getCode());
        assertEquals("C2", responses.get(1).getCode());
    }
}
