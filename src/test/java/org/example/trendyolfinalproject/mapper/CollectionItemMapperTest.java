package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.CollectionItem;
import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.model.request.CollectionItemRequest;
import org.example.trendyolfinalproject.model.response.CollectionItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CollectionItemMapperTest {

    private CollectionItemMapper collectionItemMapper;

    @BeforeEach
    void setUp() {
        collectionItemMapper = Mappers.getMapper(CollectionItemMapper.class);
    }

    @Test
    void testToEntity() {
        // given
        CollectionItemRequest request = new CollectionItemRequest();
        request.setCollectionId(1L);
        request.setProductVariantId(2L);

        // when
        CollectionItem entity = collectionItemMapper.toEntity(request);

        // then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getCollection());
        assertNotNull(entity.getProductVariant());
        assertEquals(2L, entity.getProductVariant().getId());
        assertNull(entity.getAddedAt());
    }

    @Test
    void testToResponse() {
        // given
        Product product = new Product();
        product.setName("Test Product");

        ProductVariant variant = new ProductVariant();
        variant.setId(2L);
        variant.setProduct(product);

        CollectionItem item = new CollectionItem();
        item.setId(10L);
        item.setProductVariant(variant);
        item.setAddedAt(LocalDateTime.now());

        // when
        CollectionItemResponse response = collectionItemMapper.toResponse(item);

        // then
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(2L, response.getProductVariantId());
        assertEquals("Test Product", response.getProductName());
        assertNotNull(response.getAddedAt());
    }

    @Test
    void testToResponseList() {
        // given
        ProductVariant variant1 = new ProductVariant();
        Product product1 = new Product();
        product1.setName("Product 1");
        variant1.setId(1L);
        variant1.setProduct(product1);

        ProductVariant variant2 = new ProductVariant();
        Product product2 = new Product();
        product2.setName("Product 2");
        variant2.setId(2L);
        variant2.setProduct(product2);

        CollectionItem item1 = new CollectionItem();
        item1.setId(100L);
        item1.setProductVariant(variant1);
        item1.setAddedAt(LocalDateTime.now());

        CollectionItem item2 = new CollectionItem();
        item2.setId(101L);
        item2.setProductVariant(variant2);
        item2.setAddedAt(LocalDateTime.now());

        Set<CollectionItem> items = Set.of(item1, item2);

        // when
        List<CollectionItemResponse> responses = collectionItemMapper.toResponseList(items);

        // then
        assertEquals(2, responses.size());
        assertTrue(responses.stream().anyMatch(r -> r.getProductName().equals("Product 1")));
        assertTrue(responses.stream().anyMatch(r -> r.getProductName().equals("Product 2")));
    }
}
