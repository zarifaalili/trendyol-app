package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.*;
import org.example.trendyolfinalproject.model.response.OrderItemResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemMapperTest {

    private final OrderItemMapper mapper = Mappers.getMapper(OrderItemMapper.class);

    @Test
    void testToEntity() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("49.99"));


        ProductVariant variant = new ProductVariant();
        variant.setId(2L);
        variant.setColor("Red");
        variant.setSize("M");

        BasketElement basketElement = new BasketElement();
        basketElement.setProductId(product);
        basketElement.setProductVariantId(variant);
        basketElement.setQuantity(3);
        basketElement.setAddedAt(LocalDateTime.now());

        OrderItem orderItem = mapper.toEntity(basketElement);

        assertNotNull(orderItem);
        assertNull(orderItem.getId());
        assertNull(orderItem.getOrderId());
        assertEquals(product, orderItem.getProductId());
        assertEquals(variant, orderItem.getProductVariantId());
        assertEquals(new BigDecimal("3"), orderItem.getQuantity());
        assertEquals(new BigDecimal("49.99"), orderItem.getUnitPrice());
        assertNotNull(orderItem.getCreatedAt());
    }

    @Test
    void testToResponse() {
        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));

        ProductVariant variant = new ProductVariant();
        variant.setId(20L);
        variant.setColor("Black");
        variant.setSize("15-inch");

        Order order = new Order();
        order.setId(30L);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(40L);
        orderItem.setOrderId(order);
        orderItem.setProductId(product);
        orderItem.setProductVariantId(variant);
        orderItem.setQuantity(new BigDecimal("2"));
        orderItem.setUnitPrice(new BigDecimal("999.99"));
        orderItem.setCreatedAt(LocalDateTime.now());

        OrderItemResponse response = mapper.toResponse(orderItem);

        assertNotNull(response);
        assertEquals(40L, response.getId());
        assertEquals(30L, response.getOrderId());
        assertEquals(10L, response.getProductId());
        assertEquals("Laptop", response.getProductName());
        assertEquals(20L, response.getProductVariantId());
        assertEquals("Black - 15-inch", response.getProductVariantName());
        assertEquals(new BigDecimal("2"), response.getQuantity());
        assertEquals(new BigDecimal("999.99"), response.getUnitPrice());
        assertEquals(new BigDecimal("1999.98"), response.getSubtotal());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void testMapProductVariantToName_nullFields() {
        ProductVariant variant = new ProductVariant();
        variant.setColor(null);
        variant.setSize(null);

        String result = mapper.mapProductVariantToName(variant);
        assertEquals("N/A - N/A", result);
    }

    @Test
    void testMapProductVariantToName_nullVariant() {
        String result = mapper.mapProductVariantToName(null);
        assertNull(result);
    }
}
