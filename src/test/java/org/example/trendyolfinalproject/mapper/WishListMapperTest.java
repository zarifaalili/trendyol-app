package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.*;
import org.example.trendyolfinalproject.model.response.WishListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WishListMapperTest {

    private WishListMapper wishListMapper;

    // --- Mock Entities (Replace with your actual entity classes if they exist) ---

    // Note: You must define these simple entity classes for the test to compile.
    // They are not provided in your prompt, so they are assumed here.
    // They only need the fields and getters/setters used in the mapper.

    // public class Product { ... }
    // public class ProductVariant { ... }
    // public class VariantImage { ... }
    // public class User { ... }

    // --- End Mock Entities ---


    @BeforeEach
    void setUp() {

        wishListMapper = Mappers.getMapper(WishListMapper.class);
    }

    @Test
    void testToResponse_Success() {

        Long expectedUserId = 100L;
        String expectedUserName = "John Doe";
        Long expectedProductVariantId = 200L;
        String expectedProductName = "Laptop";
        BigDecimal expectedPrice = new BigDecimal("1200.00");
        BigDecimal expectedPreviousPrice = new BigDecimal("1500.00");
        String expectedImageUrl = "http://image.url/laptop.png";
        LocalDateTime addedAt = LocalDateTime.now().minusDays(5);


        ProductImage variantImage = new ProductImage();
        variantImage.setImageUrl(expectedImageUrl);

        ProductVariant imageVariant = new ProductVariant();
        imageVariant.setId(expectedProductVariantId);
        imageVariant.setVariantImages(List.of(variantImage));

        Product product = new Product();
        product.setName(expectedProductName);
        product.setPrice(expectedPrice);
        product.setPreviousPrice(expectedPreviousPrice);
        product.setVariants(Set.of(imageVariant));

        imageVariant.setProduct(product);

        User user = new User();
        user.setId(expectedUserId);
        user.setName("John");
        user.setSurname("Doe");

        WishList wishList = new WishList();
        wishList.setId(1L);
        wishList.setUser(user);
        wishList.setProductVariant(imageVariant);
        wishList.setAddedAt(addedAt);


        WishListResponse response = wishListMapper.toResponse(wishList);


        assertNotNull(response);
        assertEquals(wishList.getId(), response.getId());
        assertEquals(expectedUserId, response.getUserId());
        assertEquals(expectedUserName, response.getUserName(), "User full name mapping failed.");
        assertEquals(expectedProductVariantId, response.getProductVariantId());
        assertEquals(expectedProductName, response.getProductName());
        assertEquals(expectedPrice, response.getProductPrice());
        assertEquals(expectedPreviousPrice, response.getPreviousPrice());
        assertEquals(addedAt, response.getAddedAt());

        assertEquals(expectedImageUrl, response.getProductImageUrl(), "Product Image URL mapping failed.");
    }
}