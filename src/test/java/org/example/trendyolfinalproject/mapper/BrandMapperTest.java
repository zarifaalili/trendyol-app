package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Brand;
import org.example.trendyolfinalproject.model.request.BrandCreateRequest;
import org.example.trendyolfinalproject.model.response.BrandResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class BrandMapperTest {

    private BrandMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BrandMapper.class);
    }

    @Test
    void testToEntity() {
        // given
        BrandCreateRequest request = new BrandCreateRequest("Apple", "Technology company");

        // when
        Brand brand = mapper.toEntity(request);

        // then
        assertNotNull(brand);
        assertNull(brand.getId()); // ignore = true olduğu üçün
        assertEquals("Apple", brand.getName());
        assertEquals("Technology company", brand.getDescription());
    }

    @Test
    void testToResponse() {
        // given
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Samsung");
        brand.setDescription("Electronics");

        // when
        BrandResponse response = mapper.toResponse(brand);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Samsung", response.getName());
        assertEquals("Electronics", response.getDescription());
    }
}
