package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Adress;
import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.dao.entity.PaymentMethod;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.CardType;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.request.OrderCreateRequest;
import org.example.trendyolfinalproject.model.response.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = Mappers.getMapper(OrderMapper.class);
    }

    @Test
    void testToEntityMapping() {
        OrderCreateRequest request = new OrderCreateRequest(1L, 2L);

        Order order = orderMapper.toEntity(request);

        assertNotNull(order);
        assertNull(order.getId());
        assertNull(order.getUser());
        assertEquals(Status.PENDING, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertNotNull(order.getOrderDate());
        assertNull(order.getTrackingNumber());
    }

    @Test
    void testMapAddressToDetails() {
        Adress adress = new Adress();
        adress.setCity("Baku");
        adress.setStreet("Nizami");
        adress.setZipCode("AZ1000");
        adress.setCountry("Azerbaijan");

        String details = orderMapper.mapAddressToDetails(adress);

        assertEquals("Baku, Nizami, AZ1000, Azerbaijan", details);
    }
    @Test
    void testToResponseMapping() {
        // Mock Address
        Adress shippingAddress = new Adress();
        shippingAddress.setId(10L);
        shippingAddress.setCity("Ganja");
        shippingAddress.setStreet("Main");
        shippingAddress.setZipCode("AZ2000");
        shippingAddress.setCountry("Azerbaijan");

        Adress billingAddress = new Adress();
        billingAddress.setId(20L);
        billingAddress.setCity("Baku");
        billingAddress.setStreet("Fountain St.");
        billingAddress.setZipCode("AZ1000");
        billingAddress.setCountry("Azerbaijan");

        // Mock User
        User user = new User();
        user.setId(5L);
        user.setSurname("Aliyev");

        // Mock Payment
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(7L);
        paymentMethod.setCardType(CardType.VISA);

        // Mock Order
        Order order = new Order();
        order.setId(99L);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("250.75"));
        order.setShippingAddressId(shippingAddress);
        order.setBillingAddressId(billingAddress);
        order.setPaymentMethodId(paymentMethod);
        order.setStatus(Status.PENDING);

        order.setTrackingNumber("123");

        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        OrderResponse response = orderMapper.toResponse(order);

        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getSurname(), response.getUserName());
        assertEquals(paymentMethod.getId(), response.getPaymentMethodId());
        assertEquals(paymentMethod.getCardType().toString(), response.getPaymentMethodName());
        assertEquals("Ganja, Main, AZ2000, Azerbaijan", response.getShippingAddressDetails());
        assertEquals("Baku, Fountain St., AZ1000, Azerbaijan", response.getBillingAddressDetails());
        assertEquals(Integer.valueOf(123), response.getTrackingNumber());
    }

}
