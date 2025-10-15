package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.BasketElement;
import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.model.request.BasketElementRequest;
import org.example.trendyolfinalproject.model.response.BasketElementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasketElementMapperTest {

    private BasketElementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BasketElementMapper.class);
    }

    @Test
    void testToEntity() {
        // given
        BasketElementRequest request = new BasketElementRequest(5L);

        // when
        BasketElement entity = mapper.toEntity(request);

        // then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getBasket());
        assertNull(entity.getProductId());
        assertNull(entity.getProductVariantId());
        assertNull(entity.getQuantity());
        assertNotNull(entity.getAddedAt());
    }

    @Test
    void testToResponse() {
        // given
        Product product = new Product();
        product.setId(2L);
        product.setName("iPhone 15");
        product.setPrice(BigDecimal.valueOf(1500));

        ProductVariant variant = new ProductVariant();
        variant.setId(3L);
        variant.setColor("Black");
        variant.setSize("128GB");

        BasketElement element = new BasketElement();
        element.setId(1L);
        element.setProductId(product);
        element.setProductVariantId(variant);
        element.setQuantity(2);
        element.setAddedAt(LocalDateTime.now());

        // when
        BasketElementResponse response = mapper.toResponse(element);

        // then
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getProductId());
        assertEquals("iPhone 15", response.getProductName());
        assertEquals(BigDecimal.valueOf(1500), response.getProductPrice());
        assertEquals(3L, response.getProductVariantId());
        assertEquals("Black - 128GB", response.getProductVariantName());
        assertEquals(2, response.getQuantity());
        assertEquals(BigDecimal.valueOf(3000), response.getSubtotal());
    }

    @Test
    void testCalculateSubtotal() {
        // given
        BigDecimal price = BigDecimal.valueOf(100);
        BigDecimal variantPrice = BigDecimal.valueOf(20);
        int quantity = 3;

        // when
        BigDecimal result = mapper.calculateSubtotal(quantity, price, variantPrice);

        // then
        assertEquals(BigDecimal.valueOf(360), result);
    }

    @Test
    void testToResponseList() {
        Product product = new Product();
        product.setId(1L);
        product.setName("MacBook");
        product.setPrice(BigDecimal.valueOf(2000));

        BasketElement element = new BasketElement();
        element.setId(10L);
        element.setProductId(product);
        element.setQuantity(1);

        List<BasketElementResponse> responseList = mapper.toResponseList(List.of(element));

        assertEquals(1, responseList.size());
        assertEquals("MacBook", responseList.get(0).getProductName());
    }
}
