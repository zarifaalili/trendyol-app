package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.PaymentMethod;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.CardType;
import org.example.trendyolfinalproject.model.enums.Currency;
import org.example.trendyolfinalproject.model.request.PaymentMethodCreateRequest;
import org.example.trendyolfinalproject.model.response.PaymentMethodResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodMapperTest {

    private PaymentMethodMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PaymentMethodMapper.class);
    }

    @Test
    void testToEntityMapping() {
        PaymentMethodCreateRequest request = PaymentMethodCreateRequest.builder()
                .cardType(CardType.VISA)
                .cardNumber("1234567812345678")
                .expirationDate(LocalDateTime.now().plusYears(2))
                .cardHolderName("Zari Alili")
                .currency(Currency.AZN)
                .build();

        PaymentMethod entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(request.getCardType(), entity.getCardType());
        assertEquals(request.getCardNumber(), entity.getCardNumber());
        assertEquals(request.getExpirationDate(), entity.getExpirationDate());
        assertEquals(request.getCardHolderName(), entity.getCardHolderName());
        assertEquals(request.getCurrency(), entity.getCurrency());
        assertNull(entity.getBalance());
    }

    @Test
    void testToResponseMapping() {
        User user = new User();
        user.setId(5L);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(10L);
        paymentMethod.setUserId(user);
        paymentMethod.setCardType(CardType.VISA);
        paymentMethod.setCardNumber("9876543210987654");
        paymentMethod.setExpirationDate(LocalDateTime.now().plusYears(1));
        paymentMethod.setCardHolderName("Zari Aliyeva");
        paymentMethod.setIsDefault(true);
        paymentMethod.setCurrency(Currency.USD);
        paymentMethod.setBalance(new BigDecimal("1500.50"));

        PaymentMethodResponse response = mapper.toResponse(paymentMethod);

        assertNotNull(response);
        assertEquals(paymentMethod.getId(), response.getId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(paymentMethod.getCardType(), response.getCardType());
        assertEquals("XXXX XXXX XXXX 7654", response.getMaskedCardNumber()); // ✅ maskCardNumber
        assertEquals(paymentMethod.getExpirationDate(), response.getExpirationDate());
        assertEquals(paymentMethod.getCardHolderName(), response.getCardHolderName());
        assertEquals(paymentMethod.getIsDefault(), response.getIsDefault());
        assertEquals(paymentMethod.getCurrency(), response.getCurrency());
        assertEquals(paymentMethod.getBalance(), response.getBalance());
    }
}
