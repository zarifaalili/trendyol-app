package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.ProductImage;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.model.response.ProductVariantResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantSimpleResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ProductVariantMapperTest {

    private final ProductVariantMapper mapper = Mappers.getMapper(ProductVariantMapper.class);

    @Test
    void testToResponseMapping() {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("100.50"));
        product.setPreviousPrice(new BigDecimal("120.00"));

        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        variant.setProduct(product);
        variant.setColor("Red");
        variant.setAttributeValue1("Cotton");
        variant.setSize("M");
        variant.setAttributeValue2("Slim");
        variant.setStockQuantity(50);
        variant.setSku("SKU123");

        ProductImage mainImage = new ProductImage();
        mainImage.setImageUrl("main.jpg");
        mainImage.setIsMainImage(true);

        ProductImage otherImage = new ProductImage();
        otherImage.setImageUrl("other.jpg");
        otherImage.setIsMainImage(false);

        variant.setVariantImages(Arrays.asList(mainImage, otherImage));

        // toResponse
        ProductVariantResponse response = mapper.toResponse(variant);
        assertNotNull(response);
        assertEquals(variant.getId(), response.getId());
        assertEquals(product.getId(), response.getProductId());
        assertEquals("Red", response.getColor());
        assertEquals("Cotton", response.getAttributeValue1());
        assertEquals("M", response.getSize());
        assertEquals("Slim", response.getAttributeValue2());
        assertEquals(50, response.getStockQuantity());
        assertEquals("SKU123", response.getSku());
        assertEquals("main.jpg", response.getImageUrl());
        assertEquals(new BigDecimal("100.50"), response.getPrice());
        assertEquals(new BigDecimal("120.00"), response.getPreviousPrice());

        // toSimpleResponse
        ProductVariantSimpleResponse simpleResponse = mapper.toSimpleResponse(variant);
        assertEquals("main.jpg", simpleResponse.getImageUrl());
        assertEquals("Red", simpleResponse.getColor());
        assertEquals("Cotton", simpleResponse.getAttributeValue1());
        assertEquals("M", simpleResponse.getSize());
        assertEquals("Slim", simpleResponse.getAttributeValue2());
        assertEquals(new BigDecimal("100.50"), simpleResponse.getPrice());
        assertEquals(new BigDecimal("120.00"), simpleResponse.getPreviousPrice());
    }

    @Test
    void testExtractMainImageUrlNoMain() {
        ProductImage img1 = new ProductImage();
        img1.setIsMainImage(false);
        img1.setImageUrl("img1.jpg");

        ProductImage img2 = new ProductImage();
        img2.setIsMainImage(false);
        img2.setImageUrl("img2.jpg");

        ProductVariant variant = new ProductVariant();
        variant.setVariantImages(Arrays.asList(img1, img2));

        String imageUrl = mapper.extractMainImageUrl(variant.getVariantImages());
        assertNull(imageUrl);
    }


}
