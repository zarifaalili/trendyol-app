package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Collection;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.request.CollectionCreateRequest;
import org.example.trendyolfinalproject.model.response.CollectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionMapperTest {

    private CollectionMapper collectionMapper;

    @BeforeEach
    void setUp() {
        collectionMapper = Mappers.getMapper(CollectionMapper.class);
    }

    @Test
    void testToEntity() {
        // given
        CollectionCreateRequest request = CollectionCreateRequest.builder()
                .name("My Collection")
                .build();

        // when
        Collection entity = collectionMapper.toEntity(request);

        // then
        assertNotNull(entity);
        assertNull(entity.getId()); // ignore = true
        assertNull(entity.getUser()); // ignore = true
        assertEquals("My Collection", entity.getName());
        assertNull(entity.getCreatedAt()); // ignore = true
        assertNull(entity.getUpdatedAt()); // ignore = true
    }

    @Test
    void testToResponse() {
        // given
        User user = new User();
        user.setId(10L);

        Collection collection = new Collection();
        collection.setId(1L);
        collection.setName("My Collection");
        collection.setUser(user);
        collection.setCreatedAt(LocalDateTime.now());
        collection.setUpdatedAt(LocalDateTime.now());

        // when
        CollectionResponse response = collectionMapper.toResponse(collection);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getUserId());
        assertEquals("My Collection", response.getName());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void testToResponseList() {
        // given
        Collection collection1 = new Collection();
        collection1.setId(1L);
        collection1.setName("Collection 1");

        Collection collection2 = new Collection();
        collection2.setId(2L);
        collection2.setName("Collection 2");

        List<Collection> collections = List.of(collection1, collection2);

        // when
        List<CollectionResponse> responses = collectionMapper.toResponseList(collections);

        // then
        assertEquals(2, responses.size());
        assertEquals("Collection 1", responses.get(0).getName());
        assertEquals("Collection 2", responses.get(1).getName());
    }
}
