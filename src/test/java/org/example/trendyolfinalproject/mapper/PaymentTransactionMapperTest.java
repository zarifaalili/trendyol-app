package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.dao.entity.PaymentMethod;
import org.example.trendyolfinalproject.dao.entity.PaymentTransaction;
import org.example.trendyolfinalproject.model.enums.Status;
import org.example.trendyolfinalproject.model.enums.Currency;
import org.example.trendyolfinalproject.model.response.PaymentTransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentTransactionMapperTest {

    private PaymentTransactionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PaymentTransactionMapper.class);
    }

    @Test
    void testToResponseMapping() {
        // Mock Order
        Order order = new Order();
        order.setId(100L);

        // Mock PaymentMethod
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(50L);
        paymentMethod.setCardNumber("9876543210987654");

        // Mock PaymentTransaction
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setId(1L);
        transaction.setOrder(order);
        transaction.setPayment(paymentMethod);
        transaction.setTransactionId(999);
        transaction.setAmount(new BigDecimal("200.75"));
        transaction.setCurrency(Currency.AZN);
        transaction.setStatus(Status.SUCCESS);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setProviderResponse("Approved");

        PaymentTransactionResponse response = mapper.toResponse(transaction);

        assertNotNull(response);
        assertEquals(transaction.getId(), response.getId());
        assertEquals(order.getId(), response.getOrderId());
        assertEquals(paymentMethod.getId(), response.getPaymentMethodId());
        assertEquals("XXXX XXXX XXXX 7654", response.getMaskedCardNumber());
        assertEquals(transaction.getTransactionId(), response.getTransactionId());
        assertEquals(transaction.getAmount(), response.getAmount());
        assertEquals(transaction.getCurrency(), response.getCurrency());
        assertEquals(transaction.getStatus(), response.getStatus());
        assertEquals(transaction.getTransactionDate(), response.getTransactionDate());
        assertEquals(transaction.getProviderResponse(), response.getProviderResponse());
    }

    @Test
    void testMaskLast4DigitsWithShortCardNumber() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setCardNumber("123");

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPayment(paymentMethod);

        PaymentTransactionResponse response = mapper.toResponse(transaction);

        assertEquals("Invalid/Unknown Card", response.getMaskedCardNumber());
    }
}
