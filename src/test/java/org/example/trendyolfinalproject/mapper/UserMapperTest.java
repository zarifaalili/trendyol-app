package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.request.UserRegisterRequest;
import org.example.trendyolfinalproject.model.response.UserProfileResponse;
import org.example.trendyolfinalproject.model.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void testToEntityMapping() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("Zari");
        request.setSurname("Ali");
        request.setEmail("zari@example.com");
        request.setPassword("password123");
        request.setConfirmedPassword("password123");
        request.setPhoneNumber("+994501234567");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        User user = mapper.toEntity(request);

        assertNotNull(user);
        assertNull(user.getId()); // id ignore edilib
        assertEquals(request.getName(), user.getName());
        assertEquals(request.getSurname(), user.getSurname());
        assertEquals(request.getEmail(), user.getEmail());
        assertEquals(request.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(request.getDateOfBirth(), user.getDateOfBirth());
        assertTrue(user.getIsActive());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertNull(user.getPasswordHash());
    }

    @Test
    void testToUserProfileResponseMapping() {
        User user = new User();
        user.setId(1L);
        user.setName("Zari");
        user.setSurname("Ali");
        user.setEmail("zari@example.com");
        user.setPhoneNumber("+994501234567");
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));

        UserProfileResponse response = mapper.toUserProfileResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals("Zari Ali", response.getFullName());
        assertEquals(user.getEmail(), response.getUsername());
        assertEquals(user.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(user.getDateOfBirth(), response.getDateOfBirth());
        assertNull(response.getAddresses());
        assertNull(response.getDefaultPaymentMethod());
        assertNull(response.getWishlistCount());
        assertNull(response.getOrderCount());
        assertNull(response.getTotalSpent());
    }

    @Test
    void testToUserResponseMapping() {
        User user = new User();
        user.setId(1L);
        user.setName("Zari");
        user.setSurname("Ali");
        user.setEmail("zari@example.com");
        user.setPhoneNumber("+994501234567");
        user.setIsActive(true);

        UserResponse response = mapper.toUserResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getSurname(), response.getSurname());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(user.getIsActive(), response.getIsActive());
    }

}
