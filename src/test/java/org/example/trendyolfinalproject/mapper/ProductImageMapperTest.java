package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.ProductImage;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.model.request.ProductImageCreateRequest;
import org.example.trendyolfinalproject.model.response.ProductImageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ProductImageMapperTest {

    private ProductImageMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductImageMapper.class);
    }

    @Test
    void testToEntityMapping() {
        ProductImageCreateRequest request = new ProductImageCreateRequest();
        request.setImageUrl("https://example.com/image.jpg");
        request.setIsMainImage(true);
        request.setDisplayOrder(1);
        request.setAltText("Sample Image");

        ProductImage entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getProduct());
        assertNull(entity.getProductVariant());
        assertEquals(request.getImageUrl(), entity.getImageUrl());
        assertEquals(request.getIsMainImage(), entity.getIsMainImage());
        assertEquals(request.getDisplayOrder(), entity.getDisplayOrder());
        assertEquals(request.getAltText(), entity.getAltText());
    }

    @Test
    void testToResponseMapping() {
        Product product = new Product();
        product.setId(100L);

        ProductImage entity = new ProductImage();
        entity.setId(10L);
        entity.setProduct(product);
        entity.setImageUrl("https://example.com/image.jpg");
        entity.setIsMainImage(true);
        entity.setDisplayOrder(1);
        entity.setAltText("Sample Image");

        ProductImageResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getImageUrl(), response.getImageUrl());
        assertEquals(entity.getIsMainImage(), response.getIsMainImage());
        assertEquals(entity.getDisplayOrder(), response.getDisplayOrder());
        assertEquals(entity.getAltText(), response.getAltText());
        assertEquals(product.getId(), response.getProductId());
    }
}
