package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.entity.UserWheel;
import org.example.trendyolfinalproject.dao.entity.Wheel;
import org.example.trendyolfinalproject.dao.entity.WheelPrize;
import org.example.trendyolfinalproject.model.request.WheelPrizeRequest;
import org.example.trendyolfinalproject.model.response.SpinWheelResponse;
import org.example.trendyolfinalproject.model.response.WheelPrizeResponse;
import org.example.trendyolfinalproject.model.response.WheelResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class WheelMapperTest {

    private WheelMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(WheelMapper.class);
    }

    @Test
    void testWheelPrizeToResponse() {
        WheelPrize prize = new WheelPrize();
        prize.setId(1L);
        prize.setName("Prize1");
        prize.setAmount(BigDecimal.valueOf(50));
        prize.setMinOrder(BigDecimal.valueOf(100));

        WheelPrizeResponse response = mapper.toResponse(prize);

        assertNotNull(response);
        assertEquals(prize.getId(), response.getId());
        assertEquals(prize.getName(), response.getName());
        assertEquals(prize.getAmount().doubleValue(), response.getAmount());
        assertEquals(prize.getMinOrder().doubleValue(), response.getMinOrder());
    }

    @Test
    void testWheelToResponse() {
        WheelPrize prize = new WheelPrize();
        prize.setId(1L);
        prize.setName("Prize1");
        prize.setAmount(BigDecimal.valueOf(50));
        prize.setMinOrder(BigDecimal.valueOf(100));

        Wheel wheel = new Wheel();
        wheel.setId(1L);
        wheel.setName("SuperWheel");
        wheel.setStartTime(LocalDateTime.now());
        wheel.setEndTime(LocalDateTime.now().plusDays(1));
        wheel.setPrizes(Collections.singletonList(prize));

        WheelResponse response = mapper.toResponse(wheel);

        assertNotNull(response);
        assertEquals(wheel.getId(), response.getId());
        assertEquals(wheel.getName(), response.getName());
        assertEquals(wheel.getStartTime(), response.getStartTime());
        assertEquals(wheel.getEndTime(), response.getEndTime());
        assertEquals(1, response.getPrizes().size());
        assertEquals(prize.getName(), response.getPrizes().get(0).getName());
    }

    @Test
    void testUserWheelToSpinWheelResponse() {
        User user = new User();
        user.setId(1L);

        Wheel wheel = new Wheel();
        wheel.setId(1L);
        wheel.setName("MegaWheel");

        WheelPrize prize = new WheelPrize();
        prize.setId(1L);
        prize.setName("Prize1");
        prize.setAmount(BigDecimal.valueOf(50));
        prize.setMinOrder(BigDecimal.valueOf(100));

        UserWheel userWheel = new UserWheel();
        userWheel.setUser(user);
        userWheel.setWheel(wheel);
        userWheel.setPrize(prize);
        userWheel.setExpiresAt(LocalDateTime.now().plusDays(1));

        SpinWheelResponse response = mapper.toResponse(userWheel);

        assertNotNull(response);
        assertEquals(wheel.getId(), response.getWheelId());
        assertEquals(wheel.getName(), response.getWheelName());
        assertEquals(prize.getId(), response.getPrizeId());
        assertEquals(prize.getName(), response.getPrizeName());
        assertEquals(prize.getAmount().doubleValue(), response.getAmount());
        assertEquals(prize.getMinOrder().doubleValue(), response.getMinOrder());
        assertEquals(userWheel.getExpiresAt(), response.getExpiresAt());
    }

    @Test
    void testWheelPrizeRequestToEntity() {
        WheelPrizeRequest request = new WheelPrizeRequest();
        request.setName("PrizeRequest");
        request.setAmount(100);
        request.setMinOrder(200);

        WheelPrize prize = mapper.toEntity(request);

        assertNotNull(prize);
        assertEquals(request.getName(), prize.getName());
        assertEquals(BigDecimal.valueOf(request.getAmount()), prize.getAmount());
        assertEquals(BigDecimal.valueOf(request.getMinOrder()), prize.getMinOrder());
        assertNull(prize.getWheel()); // ignore edilmişdir
        assertNull(prize.getId());
    }
}
